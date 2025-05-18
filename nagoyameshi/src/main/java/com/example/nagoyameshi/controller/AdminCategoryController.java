package com.example.nagoyameshi.controller;


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
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;
	
	// コメント: リダイレクト先やメッセージを定数として定義すると保守性が向上します
		
	public AdminCategoryController(CategoryRepository categoryRepository, CategoryService categoryService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}
	
	@GetMapping
	// コメント: パラメータが多く可読性が低下しています。引数を整理したり、@RequestParamを別の行に移動すると読みやすくなります
	public String index(Model model,  @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,@RequestParam(name = "keyword", required = false) String keyword) {
        Page<Category> categoryPage;
       
        // コメント: この検索ロジックはServiceレイヤーに移動することで、コントローラーの責務を減らせます
        if (keyword != null && !keyword.isEmpty()) {
            categoryPage = categoryRepository.findByNameLike("%" + keyword + "%", pageable);                
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }  
               
        model.addAttribute("categoryPage", categoryPage);   
        model.addAttribute("keyword", keyword);
       
        return "admin/categories/index";
   }
   
   @GetMapping("/{id}")
   public String show(@PathVariable(name = "id") Integer id, Model model) {
		// コメント: getReferenceByIdは存在しない場合にエラーになるため、findByIdを使ってNull判定をするか例外処理を追加すべきです
		Category category = categoryRepository.getReferenceById(id);
		model.addAttribute("category", category);
		return "admin/categories/show";
   } 
	  
   @GetMapping("/register")
   public String register(Model model) {
	     model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
	     return "admin/categories/register";
   } 

   @PostMapping("/create")
   public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
	   	if (bindingResult.hasErrors()) {
		   return "admin/categories/register";
	   	}
	   
        // コメント: カテゴリ名の重複チェックをここで行い、重複があれば適切なエラーメッセージを表示すべきです
	   	categoryService.create(categoryRegisterForm);
	   	redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");    
 
	   	return "redirect:/admin/categories";
   }  
	  
   @GetMapping("/{id}/edit")
   public String edit(@PathVariable(name = "id") Integer id, Model model) {
       // コメント: getReferenceByIdではなく、findByIdを使用してnullチェックを行い、存在しない場合は404エラーページを返すべきです
	   Category category = categoryRepository.getReferenceById(id);
	   String imageName = category.getImageName();
	   CategoryEditForm categoryEditForm = new CategoryEditForm(category.getId(), category.getName(), null);
	         
	   model.addAttribute("imageName", imageName);
	   model.addAttribute("categoryEditForm", categoryEditForm);
     
	   return "admin/categories/edit";
   }    
	   
   @PostMapping("/{id}/update")
   public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
	   if (bindingResult.hasErrors()) {
		   return "admin/categories/edit";
	   }
     
	   // コメント: 更新前に対象レコードの存在確認を行うとより安全です
       // コメント: また、カテゴリ名の重複チェックも行うべきです
	   categoryService.update(categoryEditForm);
	   redirectAttributes.addFlashAttribute("successMessage", "カテゴリを編集しました。");
     
	   return "redirect:/admin/categories";
   }    
	  
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		  // コメント: 削除前に対象レコードの存在確認を行うとより安全です
		  // また、関連するデータがある場合は削除できないようにする制約チェックも検討すべきです
          // コメント: 具体的には、このカテゴリに関連付けられたレストランが存在する場合、削除を中止して警告を表示するべきです
		  categoryRepository.deleteById(id);
	         
		  redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");
	 
		  return "redirect:/admin/categories";
	}   
}

