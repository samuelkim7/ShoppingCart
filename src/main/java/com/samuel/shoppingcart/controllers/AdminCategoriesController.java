package com.samuel.shoppingcart.controllers;

import com.samuel.shoppingcart.models.CategoryRepository;
import com.samuel.shoppingcart.models.data.Category;
import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoriesController {
  private CategoryRepository categoryRepo;

  public AdminCategoriesController(CategoryRepository categoryRepo) {
    this.categoryRepo = categoryRepo;
  }

  @GetMapping
  public String index(Model model) {
    List<Category> categories = categoryRepo.findAll();

    model.addAttribute("categories", categories);

    return "admin/categories/index";
  }

  //   @ModelAttribute("category")
  //   public Category getCategory(){
  //       return new Category();
  //   }

  @GetMapping("/add")
  public String add(Category category) {
    return "admin/categories/add";
  }

  @PostMapping("/add")
  public String add(
    @Valid Category category,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    Model model
  ) {
    if (bindingResult.hasErrors()) {
      return "admin/categories/add";
    }

    redirectAttributes.addFlashAttribute("message", "Category added");
    redirectAttributes.addFlashAttribute("alertClass", "alert-success");

    String slug = category.getName().toLowerCase().replace(" ", "-");

    Category categoryExists = categoryRepo.findByName(category.getName());

    if (categoryExists != null) {
      redirectAttributes.addFlashAttribute(
        "message",
        "Category exists, choose another"
      );
      redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
      redirectAttributes.addFlashAttribute("categoryInfo", category);
    } else {
      category.setSlug(slug);
      category.setSorting(100);

      categoryRepo.save(category);
    }

    return "redirect:/admin/categories/add";
  }

  @GetMapping("/edit/{id}")
  public String edit(@PathVariable int id, Model model) {
    Category category = categoryRepo.getOne(id);

    model.addAttribute("category", category);

    return "admin/categories/edit";
  }

  @PostMapping("/edit")
  public String edit(
    @Valid Category category,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    Model model
  ) {
    Category categoryCurrent = categoryRepo.getOne(category.getId());

    if (bindingResult.hasErrors()) {
      model.addAttribute("categoryName", categoryCurrent.getName());
      return "admin/categories/edit";
    }

    redirectAttributes.addFlashAttribute("message", "Category edited");
    redirectAttributes.addFlashAttribute("alertClass", "alert-success");

    String slug = category.getName().toLowerCase().replace(" ", "-");

    Category categoryExists = categoryRepo.findByName(category.getName());

    if (categoryExists != null) {
      redirectAttributes.addFlashAttribute(
        "message",
        "Category exists, choose another"
      );
      redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
    } else {
      category.setSlug(slug);

      categoryRepo.save(category);
    }

    return "redirect:/admin/categories/edit/" + category.getId();
  }
}
