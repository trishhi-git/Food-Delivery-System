package com.example.controller;

import com.example.entity.MenuItem;
import com.example.repository.MenuItemRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    @Autowired
    private MenuItemRepository repo;

    private static final Logger log = LoggerFactory.getLogger(MenuItemController.class);

    @PostMapping
    public MenuItem addMenuItem(@Valid @RequestBody MenuItem menuItem) {
        log.info("Adding new menu item for restaurant {}", menuItem.getRestaurantId());
        return repo.save(menuItem);
    }

    @GetMapping("/{restaurantId}")
    public List<MenuItem> getMenuByRestaurant(@PathVariable("restaurantId") Long restaurantId) {
        log.info("Fetching menu for restaurant {}", restaurantId);
        return repo.findByRestaurantId(restaurantId);
    }
}
