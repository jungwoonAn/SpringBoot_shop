package com.shop.contoller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());

        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
              Model model, @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){  // 유효성검사 필수값이 없으면
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){  // 첫번째 이미지가 없으면
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch(Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";  // 정상등록이 되면 메인 페이지로 이동
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);  // 조회한 상품 데이터를 모델에 담아 뷰로 전달
            model.addAttribute("itemFormDto", itemFormDto);
        }catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "/item/itemForm";
        }

        return "/item/itemForm";
    }

    // 상품 수정
    @PostMapping(value = "/admin/item/{itemid}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {

        if(bindingResult.hasErrors()){  // 유효성 검사 에러
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessages", "상품 수정 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";
    }

    // 상품 조회(검색 및 페이징)
    // 페이지가 없는 경우와 페이지 번호가 있는 경우 2가지 매핑
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page")Optional<Integer> page, Model model) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

        // 조회 조건과 페이징 정보를 파라미터로 받아 Page<Item> 객체를 반환
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        
        // 조회한 상품 데이터 및 페이징 정보를 뷰에 전달
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);  // 페이지 전환시 검색 조건 유지
        model.addAttribute("maxPage", 5);  // 메뉴 하단에 보여줄 페이지 번호 최대 갯수

        return "item/itemMng";
    }
}