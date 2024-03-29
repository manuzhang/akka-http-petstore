package io.github.manuzhang.petstore.controller

import java.io.File

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo
import io.github.manuzhang.petstore.model.Pet
import io.github.manuzhang.petstore.model.Pet.Status

object PetController {

  val route: Route = {
    pathPrefix("pet") {
      concat(
        pathEnd {
          concat(
            post {
              entityAs[Pet] { pet =>
                store.addPet(pet)
                reply(pet)
              }
            },
            put {
              entityAs[Pet] { pet =>
                replyPet(store.updatePet(pet))
              }
            }
          )
        },
        path("findByStatus") {
          parameter("status") { status =>
            reply(store.getPet(Status(status)))
          }
        },
        path("findByTags") {
          parameter("tags".repeated) { tags =>
            reply(store.getPet(tags.toList))
          }
        },
        pathPrefix(LongNumber) { id =>
          if (id.isNaN) {
            reply400("Invalid ID supplied")
          }
          concat(
            pathEnd {
              concat(
                get {
                  replyPet(store.getPet(id))
                },
                post {
                  parameters("name".optional, "status".optional) {
                    (name, status) => {
                      replyPet(store.updatePet(id, name, status.map(Status(_))))
                    }
                  }
                },
                delete {
                  replyPet(store.deletePet(id))
                }
              )
            },
            storeUploadedFile("uploadImage", (fileInfo: FileInfo) => new File(fileInfo.fileName)) {
              case (metadata, file) =>
                replyPet(store.updatePet(id, file.getAbsolutePath))
            }
          )
        }
      )
    }
  }

  private def replyPet(pet: Option[Pet]): Route = {
    pet match {
      case Some(p) => reply(p)
      case None => reply404("Pet not found")
    }
  }
}
