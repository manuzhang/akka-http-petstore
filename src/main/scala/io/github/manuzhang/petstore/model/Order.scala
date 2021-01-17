package io.github.manuzhang.petstore.model

import io.github.manuzhang.petstore.model.Order.Status
import io.github.manuzhang.petstore.model.Order.Status.Status
import upickle.default._

object Order {
  implicit val orderRw: ReadWriter[Order] = macroRW[Order]
  implicit val statusRw: ReadWriter[Status] =
    readwriter[String].bimap[Status](_.toString, Status.withName)

  object Status extends Enumeration {
    type Status = Value
    val Placed: Status = Value("placed")
    val Approved: Status = Value("approved")
    val Delivered: Status = Value("delivered")
  }
}

case class Order(id: Long, petId: Long, quantity: Int,
  shipDate: String, status: Status, complete: Boolean) {

  def toTable: OrderTable = OrderTable(id, petId, quantity,
    shipDate, status.toString, complete)
}

case class OrderTable(id: Long, petId: Long, quantity: Int,
  shipDate: String, status: String, complete: Boolean) {

  def toOrder: Order = Order(id, petId, quantity, shipDate, Status.withName(status), complete)
}

