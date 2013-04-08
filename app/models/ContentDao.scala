package models

import java.util.{UUID, Date => JDate}

import play.Logger
import play.api.i18n.Lang

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.session._

import controllers.DB

object ContentDao {
  import DaoParams._
  import DaoHelper._

  def getMiscContent(mcType: MiscContent.Type, lang: Lang): Option[MiscContent] = DB { session =>
    if (session.count(ks \ CF.MiscContent \ makeKey(mcType, lang)) == 0) {
      None
    } else {
      val values = session.list(ks \ CF.MiscContent \ makeKey(mcType, lang), ColumnPredicate(List("date", "title", "content")))
      val vs = mapColumns(values)

      val date = new JDate(vs.getOrElse("date", "0").toLong)

      Some(MiscContent(
        lang, mcType, date,
        vs.getOrElse("title", ""),
        vs.getOrElse("content", "")
      ))
    }
  }

  def saveMiscContent(mc: MiscContent) { DB { session =>
    val key = ks \ CF.MiscContent \ makeKey(mc.mcType, mc.lang)
    val values =
      Insert(key \ ("date", mc.date.getTime.toString)) ::
      Insert(key \ ("title", mc.title)) ::
      Insert(key \ ("content", mc.content)) :: Nil

    session.batch(values)
  }}

  /*
   * Enthusiast Sessions
   * 
   * We are very much (too much!) like relational db here:
   *
   * EnthusiastSession is a column family containing sessions details,
   * - indexed by uuid
   * - columns: title, speaker, content...etc.
   *
   * EnthusiastSessionIdsByLang is a cf container sessions ids (uuid),
   * - indexed by lang,
   * - columns: date1, date2, ..., dateN.
   *
   * NOTE: These 2 informations could be contained in one CF
   */

  def getEnthusiastSession(date: JDate, lang: Lang): Option[EnthusiastSession] = DB { session =>
    val key = ks \ CF.EnthusiastSessionIdsByLang \ byteBuffer(lang.code)

    if (session.count(key) == 0)
      None
    else {
      session.get(key \ byteBuffer(date.getTime.toString)).flatMap { column =>
        val uuid = UUID.fromString(string(column.value))
        getEnthusiastSession(uuid)
      }
    }
  }

  def getEnthusiastSession(uuid: UUID): Option[EnthusiastSession] = DB { session =>
    val key = ks \ CF.EnthusiastSession \ uuid

    if (session.count(key) == 0)
      None
    else {
      val values = session.list(key, ColumnPredicate(List("date", "lang", "speaker", "title", "content")))
      val vs = mapColumns(values)

      Some(EnthusiastSession(
        uuid,
        Lang(vs.getOrElse("lang", "en")),
        vs.get("date").map(t => new JDate(t.toLong)).getOrElse(new JDate),
        vs.getOrElse("title", ""),
        vs.getOrElse("speaker", ""),
        vs.getOrElse("content", "")
      ))
    }
  }

  def listEnthusiastSession(lang: Lang): List[EnthusiastSession] = DB { session =>
    val key = ks \ CF.EnthusiastSessionIdsByLang \ makeKey(lang)

    if (session.count(key) == 0)
      Nil
    else {
      val values = session.list(key)
      val vs = mapColumns(values)

      // TODO: quite ugly here since we execute 2 calls to cassandra for each session
      vs.map { kv =>
        getEnthusiastSession(kv._2)
      }.flatten.toList.sortBy(_.date).reverse
    }
  }

  def saveEnthusiastSession(eSession: EnthusiastSession) { DB { session =>
    // be sure of references hold by EnthusiastSessionIdsByLang
    deleteEnthusiastSession(eSession.uuid)

    val key = ks \ CF.EnthusiastSession \ eSession.uuid
    val keyLang = ks \ CF.EnthusiastSessionIdsByLang \ makeKey(eSession.lang)

    val values =
      // session itself
      Insert(key \ ("date", eSession.date.getTime.toString)) ::
      Insert(key \ ("lang", eSession.lang.code)) ::
      Insert(key \ ("title", eSession.title)) ::
      Insert(key \ ("speaker", eSession.speaker)) ::
      Insert(key \ ("content", eSession.content)) ::
      // session index in EnthusiastSessionIdsByLang
      Insert(keyLang \ (eSession.date.getTime.toString, byteBuffer(eSession.uuid.toString))) :: Nil

    session.batch(values)
  }}

  def deleteEnthusiastSession(uuid: UUID) { DB { session =>
    getEnthusiastSession(uuid).map { eSession =>
      val key = ks \ CF.EnthusiastSession \ uuid
      val keyLang = ks \ CF.EnthusiastSessionIdsByLang \ makeKey(eSession.lang)

      val values =
        Delete(key) :: Delete(keyLang \ eSession.date.getTime.toString) :: Nil

      session.batch(values)
    }
  }}

  private def makeKey(mcType: MiscContent.Type, lang: Lang) = byteBuffer(mcType.value + "_" + lang.code)

  private def makeKey(lang: Lang) = byteBuffer(lang.code)
}
