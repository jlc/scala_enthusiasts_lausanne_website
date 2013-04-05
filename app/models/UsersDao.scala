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

  def initialise() { DB { session =>
    object Default {
      val email = "admin@admin.com"
      val password = "password"
      val uuid = UUID.randomUUID()

      val keyUser = ks \ CF.User \ uuid.toString
      val keyEmailUserUUID = ks \ CF.UserIdsByEmail \ email

      val userValues =
        Insert(keyUser \ ("email", email)) :: Insert(keyUser \ ("group", Group.God().toString)) ::
        Insert(keyUser \ ("realname", "admin")) :: Insert(keyUser \ ("password", password)) :: Insert(keyUser \ ("twitter", "@admin")) :: Nil

      val emailUserValues =
        Insert(keyEmailUserUUID \ ("uuid", uuid.toString)) :: Nil
    }

    getUser(User.Email("admin@admin")).map { user =>
      Logger.info("initialise: admin user already exists ("+user.uuid.toString+")")
      Logger.info("initialise: removing previous admin user...")

      session.remove(ks \ CF.User \ user.uuid.toString)
      session.remove(ks \ CF.UserIdsByEmail \ user.email.toString)
    }

    Logger.info("initialise: creating admin user...")
    // NOTE: using '->' notation generate an '[InvalidRequestException: null]', use the second form instead
    //session.insert(Default.keyUser \ ("email" -> Default.email))
    //session.insert(Default.keyUser \ ("email", Default.email))

    session.batch(Default.userValues)
    session.batch(Default.emailUserValues)
    Logger.info("initialise: done")
  }}
}

