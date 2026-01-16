package com.jiuliu.myblog_dev.controller.categories;

public class CategoryController {
//    @RestController
//    @RequestMapping("/api/categories")
//    public class CategoryController {
//
//        /**
//         * 获取分类列表
//         * GET /api/categories
//         * 权限：category:list
//         */
//        @GetMapping
//        @CheckPermission("category:list")
//        public Result listCategories() {
//            // 返回分类列表
//        }
//
//        /**
//         * 创建分类
//         * POST /api/categories
//         * 权限：category:create
//         */
//        @PostMapping
//        @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
//        @CheckPermission("category:create")
//        public Result createCategory(@RequestBody @Valid CategoryDTO dto) {
//            // 创建分类
//        }
//
//        /**
//         * 更新分类
//         * PUT /api/categories/{id}
//         * 权限：category:edit
//         */
//        @PutMapping("/{id}")
//        @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
//        @CheckPermission("category:edit")
//        public Result updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryDTO dto) {
//            // 更新分类
//        }
//
//        /**
//         * 删除分类
//         * DELETE /api/categories/{id}
//         * 权限：category:delete
//         */
//        @DeleteMapping("/{id}")
//        @PreAuthorize("hasRole('ADMIN')")
//        @CheckPermission("category:delete")
//        public Result deleteCategory(@PathVariable Long id) {
//            // 删除分类
//        }
//    }
}
