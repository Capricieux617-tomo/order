package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final RestaurantService restaurantService;
	private final CategoryRepository categoryRepository;
	
	public AdminRestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.restaurantService = restaurantService;
		this.categoryRepository = categoryRepository;
		// コメント: 不要な空行があります
	}
	
	@GetMapping
	// コメント: パラメータの間にスペースがないので可読性が低いです。@RequestParamの前にスペースを入れるべきです
	public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,@RequestParam(name = "keyword", required = false ) String keyword) {
		Page<Restaurant> restaurantPage;
		
		// コメント: keywordの空白チェックにはStringUtils.isBlank()などを使うとより堅牢になります
		if(keyword !=null && !keyword.isEmpty()) {
			// コメント: SQL Injectionを防ぐために、クエリパラメータを使用すべきです
			restaurantPage = restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			restaurantPage = restaurantRepository.findAll(pageable);
		}
				
		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		
		return "admin/restaurants/index";
	}
			
	
	@GetMapping("{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		// コメント: getReferenceByIdではなく、findByIdを使用して存在確認をすべきです
		// 存在しない場合は404エラーを返すなどの対応が必要です
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		
		model.addAttribute("restaurant", restaurant);
		
		return "admin/restaurants/show";
	}
	
	@GetMapping("/register")
    public String register(Model model) {
    	// コメント: カテゴリリストは頻繁に使用されるのでキャッシュを検討すべきです
		List<Category> categoryList = categoryRepository.findAll();
    	model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
    	model.addAttribute("categories" , categoryList);  
    	return "admin/restaurants/register";
    	
    }
    
    @PostMapping("/create")
    public String create(@ModelAttribute @Validated RestaurantRegisterForm restaurantRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
        if (bindingResult.hasErrors()) {
        	 // コメント: 本番環境では System.out.println を適切なロガーに置き換えるべきです
        	 System.out.println(bindingResult); // 入力エラー情報のコンソール出力
        	 // コメント: エラーがある場合もカテゴリリストをモデルに追加して、フォームを再表示すべきです
        	 // return "admin/restaurants/register"; のように処理を中断すべきです
        }
        
        // コメント: try-catchブロックでサービスメソッドを呼び出し、例外処理を行うべきです
        restaurantService.create(restaurantRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");    
        
        return "redirect:/admin/restaurants";
    } 
    
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {
        // コメント: getReferenceByIdではなく、findByIdを使用して存在確認をすべきです
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        String imageName = restaurant.getImageName();

        // カテゴリのリストを取得する。
        List<Category> categoryList = categoryRepository.findAll();
        
        // コメント: マッピング処理はサービスレイヤーに移動させるべきです
        RestaurantEditForm restaurantEditForm = new RestaurantEditForm(
                restaurant.getId(), 
                restaurant.getName(), 
                null,
                restaurant.getCategory().getId(),
                restaurant.getDescription(),
                restaurant.getPrice(),
                restaurant.getPostalCode(),
                restaurant.getAddress(), 
                restaurant.getPhoneNumber(), 
                restaurant.getHolidays(),
                restaurant.getOpenTime(),
                restaurant.getCloseTime()
        		);

        model.addAttribute("imageName", imageName);
        model.addAttribute("restaurantEditForm", restaurantEditForm);
        model.addAttribute("categories", categoryList); // カテゴリのリストをモデルに追加
        
        return "admin/restaurants/edit";
    }
    
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
        if (bindingResult.hasErrors()) {
            // コメント: エラー時にはカテゴリリストとimageName情報をモデルに追加する必要があります
            return "admin/restaurants/edit";
        }
        
        // コメント: try-catchブロックで例外処理を行うべきです
        restaurantService.update(restaurantEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を編集しました。");
        
        return "redirect:/admin/restaurants";
    }    

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
        // コメント: 削除前に存在確認を行うべきです
        // コメント: 関連データ（予約など）がある場合の対処も必要です
        restaurantRepository.deleteById(id);
                
        redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
        
        return "redirect:/admin/restaurants";
    } 
}

