package io.github.manuzhang.petstore.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.manuzhang.petstore.model.Order
import upickle.default._

object OrderController {

  val route: Route = {
    pathPrefix("store") {
      concat(
        path("inventory") {
          get {
            reply(getCountByStatus)
          }
        },
        path("order") {
          post {
            entityAs[Order] { order =>
              store.addOrder(order)
              reply(order)
            }
          }
        },
        path("order" / LongNumber) { orderId =>
          concat(
            get {
              if (orderId.isNaN) {
                reply400("Invalid ID supplied")
              } else {
                store.getOrder(orderId) match {
                  case Some(order) => reply(order)
                  case None => reply404("Order not found")
                }
              }
            },
            delete {
              if (orderId.isNaN) {
                reply400("Invalid ID supplied")
              } else {
                store.deleteOrder(orderId) match {
                  case Some(_) =>
                    reply200
                  case None => reply404("Order not found")
                }
              }
            }
          )
        }
      )
    }
  }

  private def getCountByStatus: Map[String, Int] = {
    val orders = store.getOrders
    orders.groupBy(_.status.toString).mapValues(_.foldLeft(0) { case (total, order) =>
      total + order.quantity
    })
  }
}
