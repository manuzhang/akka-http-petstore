package io.github.manuzhang.petstore.model

import upickle.default._

object User {
  implicit val userRw: ReadWriter[User] = macroRW
}

case class User(id: Long, username: String, firstName: String, lastName: String, email: String,
  password: String, phone: String, userStatus: Int)
