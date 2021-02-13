package io.github.manuzhang.petstore.model

import io.github.manuzhang.petstore.model.Pet.Status.Status
import upickle.default._

object Pet {
  implicit val petRw: ReadWriter[Pet] = macroRW[Pet]
  implicit val tagRw: ReadWriter[Tag] = macroRW[Tag]
  implicit val cateRw: ReadWriter[Category] = macroRW[Category]
  implicit val statusRw: ReadWriter[Status] =
    readwriter[String].bimap[Status](_.toString, Status.withName)

  object Status extends Enumeration {
    type Status = Value
    val Available: Value = Value("available")
    val Pending: Value = Value("pending")
    val Sold: Value = Value("sold")
    
    def apply(s: String): Status = {
      Value(s)
    }
  }

  def toPet(pts: List[PetTable]): Option[Pet] = {
    toPets(pts).headOption
  }

  def toPets(pts: List[PetTable]): List[Pet] = {
    pts.groupBy(_.id).mapValues { pets =>
      val p = pets.head
      val tags = getTags(pets)
      val photoUrls = getPhotoUrls(pets)
      Pet(p.id, Category(p.categoryId, p.categoryName), p.name,
        photoUrls, tags, Status.withName(p.status))
    }.values.toList
  }

  def getTags(pets: List[PetTable]): List[Tag] = {
    pets.map(p => Tag(p.tagId, p.tagName)).distinct
  }

  def  getPhotoUrls(pets: List[PetTable]): List[String] = {
    pets.map(_.photoUrl).distinct
  }
}

case class Pet(id: Long, category: Category, name: String,
  photoUrls: List[String], tags: List[Tag], status: Status) {

  def petTable: List[PetTable] = for {
    url <- photoUrls
    tag <- tags
  } yield PetTable(id, name, status.toString, category.id, category.name, tag.id, tag.name, url)
}

case class Category(id: Long, name: String)

case class Tag(id: Int, name: String)

case class PetTable(id: Long, name: String, status: String,
  categoryId: Long, categoryName: String, tagId: Int, tagName: String, photoUrl: String)
