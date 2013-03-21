package controllers

//import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.session.{Session, Host, PoolParams, SessionPool, ExhaustionPolicy, Consistency}

object DB {
  val hosts  = Host("localhost", 9160, 250) :: Nil
  val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
  val pool   = new SessionPool(hosts, params, Consistency.One)

  def apply[T](f: (Session) => T): T = pool.borrow(f)
}
