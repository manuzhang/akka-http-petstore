package io.github.manuzhang.petstore

import io.getquill.{H2JdbcContext, SnakeCase}
import io.github.manuzhang.petstore.model._
import io.github.manuzhang.petstore.model.Pet._
import io.github.manuzhang.petstore.model.Pet.Status.Status

object Store {
  
  lazy val instance = new Store
}

class Store {
  private val ctx = new H2JdbcContext(SnakeCase, "petstore")
  
  import ctx._

  def getOrders: List[Order] = {
    ctx.run(query[OrderTable]).map(_.toOrder)
  }
  
  def getOrder(orderId: Long): Option[Order] = {
    ctx.run(query[OrderTable].filter(_.id == lift(orderId))).headOption.map(_.toOrder)
  }
  
  def deleteOrder(orderId: Long): Option[Order] = {
    ctx.transaction {
      getOrder(orderId).map { order =>
        ctx.run(query[OrderTable].filter(_.id == lift(orderId)).delete)
        order
      }
    }
  }
  
  def addOrder(order: Order): Unit = {
    ctx.run(query[OrderTable].insert(lift(order.toTable)))
  }
  
  def getPet(petId: Long): Option[Pet] = {
    toPet(ctx.run(query[PetTable].filter(_.id == lift(petId))))
  }
  
  def getPet(status: Status): List[Pet] = {
    toPets(ctx.run(query[PetTable].filter(_.status == lift(status.toString))))
  }

  def getPet(tags: List[String]): List[Pet] = {
    toPets(ctx.run(query[PetTable].filter(p => liftQuery(tags).contains(p.tagName))))
  }

  def addPet(pet: Pet): Unit = {
    ctx.run(liftQuery(pet.petTable).foreach(p => query[PetTable].insert(p)))
  }

  def updatePet(pet: Pet): Option[Pet] = {
    ctx.transaction {
      getPet(pet.id).headOption.map { _ =>
        addPet(pet)
        pet
      }
    }
  }
  
  def deletePet(petId: Long): Option[Pet] = {
    ctx.transaction {
      getPet(petId).headOption.map { pet =>
        ctx.run(query[PetTable].filter(_.id == lift(petId)).delete)
        pet
      }
    }
  }
}
