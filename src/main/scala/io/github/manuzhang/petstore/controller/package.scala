package io.github.manuzhang.petstore

import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import scala.util.{Failure, Success, Try}
import upickle.default._


package object controller {

  val store = Store.instance

  def entityAs[T: Reader](onSuccess: T => Route): Route = {
    entity(as[String]) { json =>
      Try {
        read[T](json)
      } match {
        case Success(t) =>
          onSuccess(t)
        case Failure(e) =>
          e.printStackTrace()
          reply400("Invalid input")
      }
    }
  }

  def reply[T: Writer](t: T): Route = {
    complete(HttpEntity(MediaTypes.`application/json`, write(t)))
  }

  def reply200: Route = {
    complete(StatusCodes.OK)
  }

  def reply400(msg: String): Route = {
    complete(StatusCodes.BadRequest, msg)
  }

  def reply404(msg: String): Route = {
    complete(StatusCodes.NotFound, msg)
  }
}
