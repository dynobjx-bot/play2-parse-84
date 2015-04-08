package controllers

import play.api.Play
import play.api.Play.current
import play.api.libs.json.{JsError, Json, JsObject}
import play.api.mvc.{BodyParsers, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._
import plugins.ParsePlugin
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

case class Item(objectId: Option[String], title: String, sku: String)

object Items extends Controller {
  private val parseAPI = Play.application.plugin[ParsePlugin].get.parseAPI
  implicit val itemWrites = Json.writes[Item]
  implicit val itemReads = Json.reads[Item]

  val `Items` = "Items"

  def index = Action.async { implicit request =>
    parseAPI.list(`Items`).map { res =>
      val list = ListBuffer[Item]()
      (res.json \ "results").as[List[JsObject]].map { itemJson =>
        list += itemJson.as[Item]
      }
      Ok(Json.toJson(list))
    }
  }

  def create = Action.async(BodyParsers.parse.json) { implicit request =>
    val post = request.body.validate[Item]
    post.fold(
      errors => Future.successful {
        BadRequest(Json.obj("error" -> JsError.toFlatJson(errors)))
      },
      item => {
        parseAPI.create(`Items`, Json.toJson(item)).map { res =>
          if (res.status == CREATED) {
            Created(Json.toJson(res.json))
          } else {
            BadGateway("Please try again later")
          }
        }
      }
    )
  }
}
