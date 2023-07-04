package sudoku

import zio._
import zio.json._

object Main extends ZIOAppDefault {

  object SudokuGrid {
    // Declaration of the types
    type Cell = Option[Int]
    type Grid = Vector[Vector[Cell]]

    def emptyGrid: Grid = Vector.fill(9, 9)(None)

    def getCell(grid: Grid, row: Int, col: Int): Cell =
      grid(row)(col)

    def setCell(grid: Grid, row: Int, col: Int, value: Int): Grid = {
      require(value >= 1 && value <= 9, "Value must be between 1 and 9")
      grid.updated(row, grid(row).updated(col, Some(value)))
    }

    def clearCell(grid: Grid, row: Int, col: Int): Grid = {
      grid.updated(row, grid(row).updated(col, None))
    }

    def isCellEmpty(grid: Grid, row: Int, col: Int): Boolean =
      grid(row)(col).isEmpty

    def isGridFull(grid: Grid): Boolean =
      grid.forall(row => row.forall(_.isDefined))

    // Helper function to check if a value is valid for a cell
    def isValidValue(grid: Grid, row: Int, col: Int, value: Int): Boolean = {
      val rowValues = grid(row).flatten
      val colValues = grid.map(row => row(col)).flatten
      val subgridValues = {
        val startRow = (row / 3) * 3
        val startCol = (col / 3) * 3
        grid
          .slice(startRow, startRow + 3)
          .flatMap(row => row.slice(startCol, startCol + 3))
          .flatten
      }
      !rowValues.contains(value) && !colValues.contains(value) && !subgridValues
        .contains(value)
    }
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
        case Right(grid) => Parser.printGrid(grid)
      }

    } yield ()
}
