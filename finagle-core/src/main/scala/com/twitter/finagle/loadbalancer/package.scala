package com.twitter.finagle

import com.twitter.finagle.stats.StatsReceiver

/**
 * This package implements client side load balancing algorithms.
 *
 * As an end-user, see the [[Balancers]] API to create instances which can be
 * used to configure a Finagle client with various load balancing strategies.
 *
 * As an implementor, each algorithm gets its own subdirectory and is exposed
 * via the [[Balancers]] object. Several convenient traits are provided which factor
 * out common behavior and can be mixed in (i.e. Balancer, DistributorT, NodeT,
 * and Updating).
 */
package object loadbalancer {

  @volatile private[this] var addressOrdering: StatsReceiver => Ordering[Address] =
    new Function[StatsReceiver, Ordering[Address]] {
      def apply(sr: StatsReceiver): Ordering[Address] = Address.OctetOrdering
      override def toString: String = "Address.OctetOrdering"
    }

  /**
   * Set the default [[Address]] ordering for the entire process (outside of clients
   * which override it).
   *
   * @param order A function from [[StatsReceiver]] to an [[Address]] ordering. Note,
   * the statsReceiver is passed in to allow for per-client stats on the
   * an ordering.
   *
   * @see [[LoadBalancerFactory.AddressOrdering]] for more info.
   */
  def defaultAddressOrdering(order: StatsReceiver => Ordering[Address]): Unit = {
    addressOrdering = order
  }

  /**
   * Returns the default process global [[Address]] ordering as set via
   * `defaultAddressOrdering`. If no value is set, [[Address.OctetOrdering]]
   * is used with the assumption that hosts resolved via Finagle provide the
   * load balancer with resolved InetAddresses. If a separate resolution process
   * is used, outside of Finagle, the default ordering should be overriden.
   */
  def defaultAddressOrdering: StatsReceiver => Ordering[Address] = addressOrdering
}