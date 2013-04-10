package models

import java.util.UUID

import play.Logger

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.serialization.Converter
import com.shorrockin.cascal.session.{Session, Host, PoolParams, SessionPool, ExhaustionPolicy, Consistency}
import com.shorrockin.cascal.session._

import controllers.DB

object UsersDao {
  import DaoParams._

  def authenticate(email: String, password: String): Option[User] = {
    getUser(User.Email(email)).flatMap { user =>
      if (user.password.trim == password.trim) Some(user)
      else None
    }
  }

  def getUser(email: User.Email): Option[User] = DB { session =>
    session.get(ks \ CF.UserIdsByEmail \ email.toString \ "uuid").flatMap { uuid =>
      getUser(UUID.fromString(uuid.value))
    }
  }

  /*
   * NOTE: using cascal
   * - returned value can be read only once (e.g. email.value), after it is reseted
   * - returned list may not be in the same order as given in session.list()
   */

  def getUser(uuid: UUID): Option[User] = DB { session =>
    if (session.count(ks \ CF.User \ uuid.toString) == 0)
      None
    else {
      val values =
        session.list(ks \ CF.User \ uuid.toString, ColumnPredicate(List("email", "group", "realname", "password", "twitter")))

      // NOTE: values won't be in the same order as ColumnPredicate, moreover, _.name and _.value can be read only once
      val mappedValues = values.map(v => (string(v.name) -> string(v.value))).toMap

      val email = mappedValues.getOrElse("email", "")
      val group = mappedValues.getOrElse("group", "")
      val realname = mappedValues.getOrElse("realname", "")
      val password = mappedValues.getOrElse("password", "")
      val twitter = mappedValues.getOrElse("twitter", "")

      val user = User(uuid, Group.fromName(group), realname, User.Email(email), password, twitter)

      Some(user)
    }
  }

  def saveUser(user: User) { DB { session =>
    val keyUser = ks \ CF.User \ user.uuid.toString
    val keyEmailUserUUID = ks \ CF.UserIdsByEmail \ user.email.value

    val userValues =
      Insert(keyUser \ ("email", user.email.value)) :: Insert(keyUser \ ("group", Group.God().toString)) ::
      Insert(keyUser \ ("realname", user.realname)) :: Insert(keyUser \ ("password", user.password)) ::
      Insert(keyUser \ ("twitter", user.twitter)) :: Nil

    val emailUserValues =
      Insert(keyEmailUserUUID \ ("uuid", user.uuid.toString)) :: Nil

    session.batch(userValues)
  }}

  def deleteUser(uuid: UUID) { DB { session =>
    val key = ks \ CF.User \ uuid.toString

    getUser(uuid).map { user =>
      Logger.debug("UsersDao.deleteUser: user uuid: " + uuid.toString)

      val values =
        Delete(ks \ CF.User \ user.uuid.toString) ::
        Delete(ks \ CF.UserIdsByEmail \ user.email.toString)
      session.batch(values)
    }
  }}

}

