package com.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    // 동적 쿼리 생성을 위해 JPAQueryFactory 클래스 사용
    private JPAQueryFactory queryFactory;

    // JPAQueryFactory 생성자로 EntityManager 객체에 넣어줌
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품 판매 상태 조건이 전체(null)이면 null 리턴, null이 아니면 해당 조건의 상품만 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ?
                null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // searchDateType 값에 따라 dateTime 값을 이전 시간으로 세팅후 해당시간 이후 등록된 상품만 조회
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equalsIgnoreCase("all", searchDateType) || searchDateType == null) {
            return null;
        }else if(StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        }else if(StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    // searchBy 값에 따라서 상품명 또는 생성자 아이디에서 검색어가 포함된 상품만 조회하는 조건값 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        }else if(StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    // 페이징된 상품 리스트 정보 가져오기
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        log.info("--------------------------------");
        log.info(regDtsAfter(itemSearchDto.getSearchDateType()));
        log.info(searchSellStatusEq(itemSearchDto.getSearchSellStatus()));
        log.info(searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()));
        log.info(pageable.getOffset());
        log.info(pageable.getPageSize());
        log.info("--------------------------------");

        // QueryFactory를 이용해서 검색 조건에 맞는 쿼리 생성
        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();
        
        return new PageImpl<>(content, pageable, total);
    }

    // 메인페이지에 페이징된 상품 리스트 정보 가져오기
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null :
                QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;  // id, itemNm, itemDetail, price
        QItemImg itemImg = QItemImg.itemImg;  // imgUrl

        List<MainItemDto> content = queryFactory
                .select(
                    new QMainItemDto(
                            item.id,
                            item.itemNm,
                            item.itemDetail,
                            itemImg.imgUrl,
                            item.price)
                ).from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /*
        select count(*) from item_img
        join item
        on item_img.item_id = item.item_id
        where item_img.rep_img_yn = 'Y' and item.item_nm like '%검색어%'
         */
        Long total = queryFactory.select(Wildcard.count).from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
