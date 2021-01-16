package io.github.manuzhang.petstore.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.manuzhang.petstore.model.Pet
import io.github.manuzhang.petstore.model.Pet.Status.Status

object PetController {

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
        entityAs[Status] { status =>
          reply(store.getPet(status))
        }
      },
      path("findByTags") {
        entityAs[List[String]] { tags =>
          reply(store.getPet(tags))
        }
      },
      path(LongNumber) { id =>
        if (id.isNaN) {
          reply400("Invalid ID supplied")
        }
        concat(
          get {
            replyPet(store.getPet(id))
          },
          post {
            parameters("name".optional, "status".optional) {
              (name, status) => {
                replyPet(store.updatePet(id, name, status))
              }
            }
            
          },
          delete {
            replyPet(store.deletePet(id))
          }
        )
      }
    )
  }
  
  private def replyPet(pet: Option[Pet]): Route = {
    pet match {
      case Some(p) => reply(p)
      case None => reply404("Pet not found")
    }
  }
}
