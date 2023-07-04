package sudoku

import zio._
import zio.nio.file._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

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

}
