package sudoku

import zio._
import zio.nio.file._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import sudoku.Main.SudokuGrid.{Grid, emptyGrid}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object Parser {

  def readFile(path: String): Task[String] =
    ZIO
      .succeed(Files.exists(Paths.get(path)))
      .flatMap { exists =>
        if (!exists)
          ZIO.fail(new Exception("File does not exist, please try again"))
        else ZIO.succeed(Files.readAllBytes(Paths.get(path)))
      }
      .map(bytes => new String(bytes, StandardCharsets.UTF_8))

  def readGridFromFile(file: String): Either[Exception, Grid] = {
    decode[Grid](file)
      .fold(
        error => Left(new Exception(error)),
        grid => Right(grid)
      )
  }

  def printGrid(grid: Grid): Task[Unit] = {
    val gridString = grid
      .map(row => row.map(cell => cell.getOrElse(" ")).mkString(" "))
      .mkString("\n")
    ZIO.succeed(println(gridString))
  }

}
