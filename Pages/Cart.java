import java.util.ArrayList;
import java.util.List;

/**
 * Cart - 简单的购物车单例类
 *
 * 设计说明：
 * - 使用单例模式（`getInstance()`）保证全局唯一购物车实例。
 * - 使用内部静态类 `CartItem` 表示购物项，内部以 `List<CartItem>` 存储。
 * - 提供增、删、清空、获取列表和计算总价的便捷方法。
 */
public class Cart {
    // 单例实例
    private static Cart instance;
    // 存放购物项的列表
    private List<CartItem> items;

    // 私有构造函数：初始化商品列表
    private Cart() {
        items = new ArrayList<>();
    }

    /**
     * 获取全局唯一购物车实例（懒汉式单例）
     *
     * @return Cart 单例实例
     */
    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    /**
     * 向购物车添加商品：若已存在同 id 项则累计数量，否则新增条目。
     *
     * @param id 商品唯一标识
     * @param name 商品名称
     * @param price 单价
     * @param quantity 数量（应为正数）
     */
    public void addItem(String id, String name, double price, int quantity) {
        // 若已存在同 id 的条目则增加数量并返回
        for (CartItem item : items) {
            if (item.getId().equals(id)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // 否则新增条目
        items.add(new CartItem(id, name, price, quantity));
    }

    /**
     * 根据商品 id 从购物车移除对应条目
     *
     * @param id 要移除的商品 id
     */
    public void removeItem(String id) {
        items.removeIf(item -> item.getId().equals(id));
    }

    /** 清空购物车 */
    public void clear() {
        items.clear();
    }

    /**
     * 获取购物车内部存放的商品列表引用（注意：直接修改该列表可能导致不一致）
     *
     * @return 商品列表
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * 计算购物车中所有商品的总价（不包含税费或运费）
     *
     * @return 总价
     */
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    /**
     * 表示购物车中的单个商品条目
     */
    public static class CartItem {
        private String id;
        private String name;
        private double price;
        private int quantity;

        public CartItem(String id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        /** 获取商品唯一 id */
        public String getId() { return id; }

        /** 获取商品名称 */
        public String getName() { return name; }

        /** 获取商品单价 */
        public double getPrice() { return price; }

        /** 获取商品数量 */
        public int getQuantity() { return quantity; }

        /** 设置商品数量（调用方应保证数量合理） */
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        @Override
        public String toString() {
            return String.format("%s - %.2f x %d", name, price, quantity);
        }
    }
}
