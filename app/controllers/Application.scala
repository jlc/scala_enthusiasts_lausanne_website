package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.session.{Host, PoolParams, SessionPool, ExhaustionPolicy, Consistency}

object Application extends Controller with ControllerHelper {

  def index() = Action { implicit request =>

    val hosts  = Host("localhost", 9160, 250) :: Nil
    val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
    val pool   = new SessionPool(hosts, params, Consistency.One)

    pool.borrow { session =>
      session.insert("segl" \ "Users" \ "key" \ ("realname", "jeanluc"))

      val value = session.get("segl" \ "Users" \ "key" \ "realname")
      println("XXX after inserting, here is the value: " + value)
    }

    Ok(views.html.index()(clientLanguage))
  }

  def meetings = Action { implicit request =>
    Ok(views.html.meetings()(clientLanguage))
  }

  def address = Action { implicit request =>
    Ok(views.html.address()(clientLanguage))
  }

  def sponsors = Action { implicit request =>
    Ok(views.html.sponsors()(clientLanguage))
  }
  
  def lang(l: String) = Action { implicit request =>
    def redirect = Redirect(routes.Application.index())

    Lang.get(l) map { lang =>
      Logger.debug("lang: Setting lang to: " + l)
      redirect.withSession(SessionValue.Lang -> l)
    } getOrElse {
      Logger.debug("lang: Removing previous setting of lang")
      redirect.withSession(session - SessionValue.Lang)
    }
  }

}
