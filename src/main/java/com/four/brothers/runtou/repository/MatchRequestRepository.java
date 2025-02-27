package com.four.brothers.runtou.repository;

import com.four.brothers.runtou.domain.MatchRequest;
import com.four.brothers.runtou.domain.Matching;
import com.four.brothers.runtou.domain.OrderSheet;
import com.four.brothers.runtou.domain.Performer;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class MatchRequestRepository {
  @PersistenceContext
  EntityManager em;

  /**
   * 매칭요청 저장 메서드
   * @param orderSheet 요청할 주문서
   * @param performer 매칭을 요청한 수행자
   */
  public void saveMatchRequest(OrderSheet orderSheet, Performer performer) {
    MatchRequest matchRequest = new MatchRequest(orderSheet, performer, false, false);
    em.persist(matchRequest);
  }

  /**
   * 모든 매칭요청을 조회하는 메서드
   * @param nowPage
   * @param itemSize
   * @return
   */
  public List<MatchRequest> findAll(int nowPage, int itemSize) {
    if (nowPage < 1) {
      throw new IllegalArgumentException("조회할 현재 페이지는 1 이상이어야 합니다.");
    }
    if (itemSize < 1) {
      throw new IllegalArgumentException("한번에 조회할 수 있는 엔티티의 개수는 1 이상이어야 합니다.");
    }

    String jpql = "select p from MatchRequest p";
    List<MatchRequest> resultList = em.createQuery(jpql, MatchRequest.class)
      .setFirstResult((nowPage - 1) * itemSize)
      .setMaxResults(itemSize)
      .getResultList();

    return resultList;
  }

  /**
   * 매칭요청 pk값으로 매칭요청을 찾는 메서드
   * @param matchRequestPk
   * @return
   */
  public Optional<MatchRequest> findById(long matchRequestPk) {
    MatchRequest matchRequest = em.find(MatchRequest.class, matchRequestPk);
    return Optional.ofNullable(matchRequest);
  }

  /**
   * 해당 요청서에 대해 요청된 매칭요청을 모두 찾는 메서드
   * @param orderSheet 매칭요청을 찾을 요청서
   * @return
   */
  public List<MatchRequest> findByOrderSheet(OrderSheet orderSheet) {
    String jpql = "select m from MatchRequest m " +
      "where m.orderSheet = :orderSheet";

    List<MatchRequest> result = em.createQuery(jpql, MatchRequest.class)
      .setParameter("orderSheet", orderSheet)
      .getResultList();

    return result;
  }

  /**
   * '매칭을 요청할 요청서'와 '매칭을 요청한 수행자'로 매칭요청을 찾는 메서드
   * @param orderSheet 매칭을 요청할 요청서
   * @param performer 매칭을 요청한 수행자
   * @return
   */
  public Optional<MatchRequest> findByOrderSheetAndPerform(OrderSheet orderSheet, Performer performer) {
    MatchRequest result;
    String jpql = "select m from MatchRequest m " +
      "where m.orderSheet = :orderSheet " +
      "and m.performer = :performer";

    TypedQuery<MatchRequest> query = em.createQuery(jpql, MatchRequest.class)
      .setParameter("orderSheet", orderSheet)
      .setParameter("performer", performer);

    try {
      result = query.getSingleResult();
    } catch (NoResultException e1) {
      return Optional.empty();
    }

    return Optional.of(result);
  }

  /**
   * pk값으로 삭제하는 메서드
   * @param pk
   */
  public void deleteMatchingRequestById(long pk) {
    String jpql = "delete from MatchRequest o " +
      "where o.id = :pk";
    em.createQuery(jpql).setParameter("pk", pk).executeUpdate();
  }
}
