package com.busapp.buss_api.repository;

import com.busapp.buss_api.entity.PickupDropoffPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupDropoffPointRepository extends JpaRepository<PickupDropoffPoint, Integer> {

    List<PickupDropoffPoint> findByRouteIdAndPointType(Integer routeId, PickupDropoffPoint.PointType pointType);

    List<PickupDropoffPoint> findByOperatorIdAndRouteIdAndPointType(
            Integer operatorId, Integer routeId, PickupDropoffPoint.PointType pointType);

    List<PickupDropoffPoint> findByRouteId(Integer routeId);
}
