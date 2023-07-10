import io.circe.Json
import io.circe.syntax._
import zio._
import zio.json._

import scala.io.Source
import java.io.PrintWriter
import java.io.File

object Main extends ZIOAppDefault {
  type Grid = List[List[Int]]   // represents the Sudoku grid as a list of lists of integers
  type JsonExt = (IO[String, Sudoku], Grid)   // represents a tuple containing an IO effect for JSON parsing and the grid data.

  case class Sudoku(RawData: List[List[Int]])
    // represents the structure of the Sudoku puzzle. It has a single field RawData,
    // which is a list of lists of integers representing the Sudoku grid.
  object Sudoku {
    implicit val decoder: JsonDecoder[Sudoku] = DeriveJsonDecoder.gen[Sudoku]
    implicit val encoder: JsonEncoder[Sudoku] = DeriveJsonEncoder.gen[Sudoku]
  }
    // enable encoding instances of Sudoku to JSON and decoding JSON into instances of Sudoku.

  def uploadJson(path: String): JsonExt = {
    val file = Source.fromFile(path).mkString
    print(s"$file \n")
    val sudoku = file.fromJson[Sudoku]
    val jsonIO = ZIO.fromEither(file.fromJson[Sudoku])
    sudoku match {
      case Right(i) =>
        val rawdata = i.RawData
        (jsonIO, rawdata)
      case Left(i) =>
        print(s"Error: $i")
        throw new RuntimeException("JSON parsing error")
    }
  }
  //  takes a file path as input, reads the content of the file, and attempts to parse it as JSON into an instance of Sudoku.
  //  It returns a tuple (jsonIO, rawdata), where jsonIO is an IO effect representing the parsing result,
  //  and rawdata is the grid data extracted from the parsed Sudoku instance.

  def checkValidity(grid: Grid, digit: Int, position: Vector[Int]): Boolean = {
    val row = position.head
    val col = position(1)
    val rowCheck = !grid(row).contains(digit)
    val colCheck = !grid.exists(row => row(col) == digit)
    val subgridCheck = {
      val startRow = (row / 3) * 3
      val startCol = (col / 3) * 3
      val subgrid = grid.slice(startRow, startRow + 3).flatMap(row => row.slice(startCol, startCol + 3))
      !subgrid.contains(digit)
    }
    rowCheck && colCheck && subgridCheck
  }
  // checks if a given digit is valid to be placed at a specific position in the Sudoku grid.
  // It checks the digit's validity in the same row, column, and 3x3 subgrid.
  // It returns a Boolean indicating whether the digit is valid or not.


  def findNullPosition(grid: Grid): Option[Vector[Int]] = {
    grid.zipWithIndex.flatMap { case (row, rowIndex) =>
      row.zipWithIndex.collect { case (0, colIndex) =>
        Vector(rowIndex, colIndex)
      }
    }.headOption
  }
  // finds the first empty position (0) in the Sudoku grid represented by the list of lists.
  // It returns an Option[Vector[Int]] containing the position as a vector of row and column indices.

  def solve(grid: Grid): Option[Grid] = {
    findNullPosition(grid) match {
      case Some(position) =>
        (1 to 9).to(LazyList).flatMap { digit =>
          if (checkValidity(grid, digit, position)) {
            solve(grid.updated(position.head, grid(position.head).updated(position(1), digit)))
          } else {
            LazyList.empty
          }
        }.headOption
      case None => Some(grid)
    }
  }
  //  It recursively searches for empty positions, tries different digits (1-9) at each position, and checks their validity.
  //  It returns an Option[Grid] representing the solved Sudoku grid if a solution is found, or None if no solution exists.


  def printGrid(grid: Grid): Unit = {
    grid.foreach(row => println(row.mkString(" ")))
    println()
  }
  // Prints the given Sudoku grid by iterating over each row and printing its elements joined by spaces.
  // It adds an empty line after printing the grid.



  def solveSudoku(grid: Grid): ZIO[Any, Any, Any] = {
    ZIO.fromOption(solve(grid)).fold(
      _ => Console.print("Sorry, we didn't find solution for your problem"),
      solution => {
        printGrid(solution)
        convertToJson(solution)
      }
    )
  }
  // It prints the initial grid, attempts to solve it, and if a solution is found, prints the solved grid and
  // writes it to a JSON file using the convertToJson function.


  def convertToJson(grid: Grid): Unit = {
    val json = Json.obj(
      "RawData" -> grid.asJson
    )
    val jsonString = json.spaces2
    val writer = new PrintWriter(new File(s"data_test/result/solved.json"))
    writer.write(jsonString)
    writer.close()
  }
  // takes a solved Sudoku grid and converts it to a JSON object using the as Json syntax from Circe.
  // It then writes the JSON object as a formatted string to a file named "solved.json".


  def run: ZIO[Any, Any, Unit] =
    for {
      _ <- Console.print("Enter the path to the JSON file containing the Sudoku problem:")
      path <- Console.readLine
      _ <- Console.printLine(s"You entered: $path")
      gridIO = uploadJson(path)
      _ <- solveSudoku(gridIO._2)
    } yield ()
}
