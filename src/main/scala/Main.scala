package sudoku

import zio._
import zio.json._
import scala.annotation.tailrec

object Main extends ZIOAppDefault {

  object SudokuGrid {
    // Declaration of the types
    type Cell = Option[Int]
    type Grid = Vector[Vector[Cell]]

    def validate(sudoku: Grid, x: Int, y: Int, value: Int): Boolean = {
      val row = sudoku(y).flatten
      val rowProperty = !row.contains(value)

      val column = sudoku.map(r => r.apply(x)).flatten
      val columnProperty = !column.contains(value)

      val boxX = x / 3
      val boxY = y / 3
      val box = for {
        yb <- (boxY * 3) until (boxY * 3 + 3) // indices for rows in THIS box
        xb <- (boxX * 3) until (boxX * 3 + 3) // same for cols
      } yield sudoku(yb)(xb)
      val boxProperty = !box.contains(value)

      rowProperty && columnProperty && boxProperty
    }

    def solve(sudoku: Grid, x: Int = 0, y: Int = 0): Option[Grid] = {
      if (y >= 9) Some(sudoku) // final solution
      else if (x >= 9) solve(sudoku, 0, y + 1) // need to fill in the next row
      else if (sudoku(y)(x).isDefined)
        solve(
          sudoku,
          x + 1,
          y
        ) // need to fill in the next cell (cell to the right)
      else {
        // Try filling the cell with a value from 1 to 9
        val possibleValues =
          (1 to 9).filter(value => validate(sudoku, x, y, value))
        // Try each possible value for the current cell
        possibleValues.foldLeft(Option.empty[Grid]) { (acc, value) =>
          acc match {
            case Some(_) =>
              acc // A solution has been found, just propagate it up
            case None =>
              // Fill the sudoku board with the value
              val newSudoku =
                sudoku.updated(y, sudoku(y).updated(x, Some(value)))
              // Try the next cell
              solve(newSudoku, x + 1, y) match {
                case Some(solution) =>
                  Some(solution) // Propagate the solution up
                case None =>
                  None // This value doesn't lead to a solution, try the next one
              }
          }
        }
      }
    }

    def isGridFull(grid: Grid): Boolean = !grid.flatten.contains(None)
  }

  def run: ZIO[Any, Throwable, Unit] =
    for {
      _ <- Console.print(
        "Enter the path to the JSON file containing the Sudoku problem:"
      )
      path <- Console.readLine
      _ <- Console.printLine(s"You entered: $path")
      // Add your Sudoku solver logic here, utilizing ZIO and interacting with the ZIO Console
      json <- Parser.readFile(path)

      grid = Parser.readGridFromFile(json)

      // _ <- Console.printLine(s"Grid: ${grid.toString()}")
      result <- grid match {
        case Left(error) => Console.printLine(s"Error: ${error.getMessage()}")
        case Right(grid) =>
          Parser.printGrid(grid)
          Console.printLine("__________")
          if (SudokuGrid.isGridFull(grid)) {
            // The grid is already solved
            Console.printLine("The Sudoku is already solved")
          } else {
            SudokuGrid.solve(grid) match {
              case Some(solution) =>
                Console
                  .printLine("Solution:")
                  .flatMap(_ => Parser.printGrid(solution))
              case None =>
                Console.printLine("No solution found for the Sudoku puzzle.")
            }
          }
      }

    } yield ()

}
