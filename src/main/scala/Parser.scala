package sudoku

import zio._
import zio.nio.file._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import sudoku.Main.SudokuGrid.{Grid}
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
    val separator = "---------+---------+--------"

    val gridString = grid.zipWithIndex.map { case (row, rowIndex) =>
      val formattedRow = row.zipWithIndex.map { case (cell, colIndex) =>
        val value = cell.getOrElse(" ")
        if ((colIndex + 1) % 3 == 0 && colIndex != 8) s" $value |"
        else s" $value "
      }.mkString

      if ((rowIndex + 1) % 3 == 0 && rowIndex != 8)
        s"$formattedRow\n$separator\n"
      else s"$formattedRow\n"
    }.mkString

    ZIO.succeed(println(s"$separator\n$gridString$separator"))
  }

}
