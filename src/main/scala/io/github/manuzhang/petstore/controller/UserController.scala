package io.github.manuzhang.petstore.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.manuzhang.petstore.model.User

object UserController {

  pathPrefix("user") {
    concat(
      pathEnd {
        post {
          entityAs[User] { user =>
            store.addUser(user)
            reply(user)
          }
        }
      },
      path("login") {
        get {
          parameters("username", "password") { (username, password) =>
            store.getUser(username) match {
              case Some(user) if user.password == password => reply200
              case _ => reply400("Invalid username/password supplied")
            }
          }
        }
      },
      path(Segment) { username =>
        concat(
          get {
            replyUser(store.getUser(username))
          },
          put {
            entityAs[User] { user =>
              replyUser(store.updateUser(username, user))
            }
          },
          delete {
            replyUser(store.deleteUser(username))
          }
        )
      }
    )
  }

  private def replyUser(user: Option[User]): Route = {
    user match {
      case Some(u) => reply(u)
      case None => reply404("User not found")
    }
  }
}
