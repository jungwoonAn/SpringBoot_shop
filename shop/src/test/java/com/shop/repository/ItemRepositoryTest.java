package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setItemSellStatus(ItemSellStatus.SELL);

        Item savedItem = itemRepository.save(item);

        System.out.println(savedItem);
    }

    @Test
    @DisplayName("레코드 삭제")
    public void deleteItemTest() {
        itemRepository.deleteById(2L);
    }

    @Test
    @DisplayName("레코드 조회")
    public void findItemTest() {
        Optional<Item> item = itemRepository.findById(1L);
        item.ifPresent(System.out::println);
    }

    @Test
    @DisplayName("상품 여러개 저장 테스트")
    public void createItemTest2() {
        for(int i=0; i<10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setItemSellStatus(ItemSellStatus.SELL);
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("레코드 갯수 조회")
    public void countItemTest() {
        long count = itemRepository.count();
        System.out.println("count: " + count);
    }

    @Test
    @DisplayName("전체 레코드 조회")
    public void selectAllTest(){
        List<Item> items = itemRepository.findAll();
        items.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("레코드 수정")
    public void selectByIdTest() {
        Optional<Item> result = itemRepository.findById(1L);
        Item item = result.get();

        item.setItemNm("수정된 상품 이름");
        item.setPrice(9999);

        itemRepository.save(item);
    }

    @Test
    @DisplayName("상품명 조회")
    public void selectByItemNmTest() {
        String name="수정된 상품 이름";
        List<Item> itemNm = itemRepository.findByItemNm(name);

        itemNm.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("상품명 조회(와일드카드)")
    public void selectByItemNmContaining(){
        String name="수정";
        List<Item> itemNm = itemRepository.findByItemNmContaining(name);

        itemNm.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("price 가격 이상 조회")
    public void selectByItemPriceGreaterThan(){
        List<Item> items = itemRepository.findItemByPriceGreaterThan(10000);

        items.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("price order by desc")
    public void selectByItemNmContainingOrderByPriceDesc(){
        List<Item> items = itemRepository.findItemByItemNmContainingOrderByPriceDesc("상품");

        items.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("상품명 or 상품상세설명 테스트")
    public void findByItemNmOrItemDetailTest(){
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");

        itemList.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void selectByItemDetail(){
        List<Item> items = itemRepository.findByItemDetail("상품");

        items.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("@Query-nativeQuery 속성을 이용한 상품 조회 테스트")
    public void selectByItemDetail2(){
        List<Item> items = itemRepository.findByItemDetail2("상품");

        items.forEach(item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%테스트 상품 상세 설명%"))
                .orderBy(qItem.price.desc());
        /*
        select * from item
        where item_sell_status like "%SELL%" or item_detail like "%테스트 상품 상세 설명%"
        order by price desc;
        */
        List<Item> itemList = query.fetch();
        itemList.forEach(item -> log.info(item.toString()));

//        long count = query.fetchCount();  // 'fetchCount()' is deprecated
//        log.info("count: " + count);

        int total = itemList.size();
        log.info("total: " + total);
    }

    public void createItemList2(){
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트2")
    public void queryDslTest2(){
        this.createItemList2();

        // 쿼리에 들어갈 조건을 만들어주는 빌더
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        // 조회시 필요한 and 조건 추가
        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        // PageRequest.of() 메서드로 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 5);

        // findAll() 메서드를 이용해 조건에 맞는 데이터를 Page 객체로 받아옴
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        log.info("total elements : " + itemPagingResult.getTotalElements());

        itemPagingResult.getContent().forEach(resultItem -> log.info(resultItem.toString()));
    }
}