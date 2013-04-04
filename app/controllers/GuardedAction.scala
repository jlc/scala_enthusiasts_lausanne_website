package controllers

import play.api._
import play.api.mvc._

import play.api.Logger
import play.api.i18n.Lang
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json.Json

import models.{User, Group, UsersDao}

trait GuardedAction extends AuthHelper with SpyHelper {

  type Spyable = Boolean

  protected def apply(block: (Request[AnyContent]) => (User) => Result)(implicit spy: Spyable) = Action { implicit request =>
    loggedAs { user =>
      if (spy) informOtherClient

      block(request)(user)
    }
  }

  protected def apply(group: Group)(block: (Request[AnyContent]) => (User) => Result)(implicit spy: Spyable) = Action { implicit request =>
    ensureMembership(group) { user =>
      if (spy) informOtherClient

      block(request)(user)
    }
  }

}

object GuardedAction extends GuardedAction {

  implicit val spyable = true

  def apply(block: (Request[AnyContent]) => (User) => Result) =
    super.apply(block)

  def restrictedTo(group: Group)(block: (Request[AnyContent]) => (User) => Result) =
    super.apply(group)(block)

}

object DiscretGuardedAction extends GuardedAction {

  implicit val spyable = false

  def apply(block: (Request[AnyContent]) => (User) => Result) =
    super.apply(block)

  def restrictedTo(group: Group)(block: (Request[AnyContent]) => (User) => Result) =
    super.apply(group)(block)

}
