package sudoku

import zio._
import zio.nio.file._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import zio.json._
import zio.json.JsonDecoderOps._

object Parser {

  // Define a case class to represent a cell in the Sudoku grid
  case class Cell(value: Option[Int])

  // Define a decoder for the Cell case class
  implicit val cellDecoder: JsonDecoder[Cell] = DeriveJsonDecoder.gen[Cell]

  def readFile(path: String): Task[String] =
    ZIO
      .succeed(Files.exists(Paths.get(path)))
      .flatMap { exists =>
        if (!exists)
          ZIO.fail(new Exception("File does not exist, please try again"))
        else ZIO.succeed(Files.readAllBytes(Paths.get(path)))
      }
      .map(bytes => new String(bytes, StandardCharsets.UTF_8))
  def parseSudoku(json: String): Task[Vector[Vector[Cell]]] =
    ZIO.fromEither(
      json.fromJson[List[List[Option[Int]]]].map(_.map(_.map(Cell)))
    )
}
