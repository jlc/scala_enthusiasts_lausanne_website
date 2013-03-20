package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json.Json

/*
import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.session.{Host, PoolParams, SessionPool, ExhaustionPolicy, Consistency}
 */

import models.User

object Application extends Controller with ControllerHelper {

  def index() = Action { implicit request =>

    /*
    val hosts  = Host("localhost", 9160, 250) :: Nil
    val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
    val pool   = new SessionPool(hosts, params, Consistency.One)

    pool.borrow { session =>
      session.insert("segl" \ "Users" \ "key" \ ("realname", "jeanluc"))

      val value = session.get("segl" \ "Users" \ "key" \ "realname")
      println("XXX after inserting, here is the value: " + value)
    }
     */

    Ok(views.html.index()(User.anonymous, clientLanguage))
  }

  def meetings = Action { implicit request =>
    Ok(views.html.meetings()(User.anonymous, clientLanguage))
  }

  def address = Action { implicit request =>
    Ok(views.html.address()(User.anonymous, clientLanguage))
  }

  def sponsors = Action { implicit request =>
    Ok(views.html.sponsors()(User.anonymous, clientLanguage))
  }
  
  def lang(l: String) = Action { implicit request =>
    def redirect = Redirect(routes.Application.index())

    Lang.get(l) map { lang =>
      Logger.debug("lang: Setting lang to: " + l)
      redirect.withSession(SessionKey.Lang -> l)
    } getOrElse {
      Logger.debug("lang: Removing previous setting of lang")
      redirect.withSession(session - SessionKey.Lang)
    }
  }

  def admin = Action { implicit request =>
    Ok("")
  }

  // Remote procedure

  def authenticate = Action { implicit request =>

    val loginForm = Form(
      tuple(
        "email" -> email,
        "password" -> nonEmptyText
      )
    )

    val json =
      loginForm.bindFromRequest.fold(
        loginWithErrors => {
          Json.obj("return" -> false, "error" -> "Invalid authentication")
        },
        login => {
          Logger.debug("authenticate: " + login)
          Json.obj("return" -> true)
        }
      )

    Ok(Json.stringify(json))
  }
}
