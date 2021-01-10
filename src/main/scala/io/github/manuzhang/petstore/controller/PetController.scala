package io.github.manuzhang.petstore.controller

import akka.http.scaladsl.server.Directives._
import io.github.manuzhang.petstore.model.Pet
import io.github.manuzhang.petstore.model.Pet.Status.Status

class PetController {

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
              store.updatePet(pet) match {
                case Some(_) => reply(pet)
                case None => reply404("Pet not found")
              }
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
            store.getPet(id) match {
              case Some(pet) => reply(pet)
              case None => reply404("Pet not found")
            }
          },
          delete {
            store.deletePet(id) match {
              case Some(pet) => reply200
              case None => reply404("Pet not found")
            }
          }
        )
      }
    )
  }
}
