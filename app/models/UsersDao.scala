package models

import play.Logger

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.serialization.Converter
import com.shorrockin.cascal.session.{Session, Host, PoolParams, SessionPool, ExhaustionPolicy, Consistency}
import com.shorrockin.cascal.session._

import controllers.DB

object UsersDao {

  def authenticate(email: String, password: String): Boolean = DB { session =>
    session.get("segl" \ "Users" \ email \ "password").
      map(_.value.trim == password.trim).
      getOrElse(false)
  }

  def getUser(email: String): User = DB { session =>
    val List(groupName, realname, twitter) =
      session.list("segl" \ "Users" \ email, ColumnPredicate(List("group", "realname", "twitter")))

    val group = Group.fromName(groupName.value)

    val user = User(group, realname.value, email.value, twitter.value)

    Logger.debug("getUser: " + user)
    user
  }
}

