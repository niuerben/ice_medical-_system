import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * OrderHistory - 订单历史管理类
 * 用于记录和管理用户的购买历史
 */
public class OrderHistory {
  // 单例实例
  private static OrderHistory instance;
  // 存放订单的列表
  private List<Order> orders;
  // 订单ID计数器
  private int orderIdCounter = 1;

  // 私有构造函数：初始化订单列表
  private OrderHistory() {
    orders = new ArrayList<>();
  }

  /**
   * 获取全局唯一订单历史实例
   * 
   * @return OrderHistory 单例实例
   */
  public static OrderHistory getInstance() {
    if (instance == null) {
      instance = new OrderHistory();
    }
    return instance;
  }

  /**
   * 创建新订单
   * 
   * @param items      订单商品列表
   * @param totalPrice 订单总价
   * @return 创建的订单
   */
  public Order createOrder(List<OrderItem> items, double totalPrice) {
    Order order = new Order("ORD" + String.format("%04d", orderIdCounter++), items, totalPrice);
    orders.add(order);
    return order;
  }

  /**
   * 获取所有订单
   * 
   * @return 订单列表
   */
  public List<Order> getOrders() {
    return orders;
  }

  /**
   * 表示一个订单
   */
  public static class Order {
    private String orderId;
    private List<OrderItem> items;
    private Date orderDate;
    private double totalPrice;
    private String status;

    public Order(String orderId, List<OrderItem> items, double totalPrice) {
      this.orderId = orderId;
      this.items = new ArrayList<>(items);
      this.orderDate = new Date();
      this.totalPrice = totalPrice;
      this.status = "已完成";
    }

    public String getOrderId() {
      return orderId;
    }

    public List<OrderItem> getItems() {
      return items;
    }

    public Date getOrderDate() {
      return orderDate;
    }

    public double getTotalPrice() {
      return totalPrice;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
  }

  /**
   * 表示订单中的单个商品
   */
  public static class OrderItem {
    private String id;
    private String name;
    private String category;
    private double price;
    private int quantity;

    public OrderItem(String id, String name, String category, double price, int quantity) {
      this.id = id;
      this.name = name;
      this.category = category;
      this.price = price;
      this.quantity = quantity;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getCategory() {
      return category;
    }

    public double getPrice() {
      return price;
    }

    public int getQuantity() {
      return quantity;
    }
  }
}