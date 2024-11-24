package org.edu.usco.pw.ms_official.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.edu.usco.pw.ms_official.model.*;
import org.edu.usco.pw.ms_official.repository.OrderRepository;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.edu.usco.pw.ms_official.service.OrderService;
import org.edu.usco.pw.ms_official.service.ProductService;
import org.edu.usco.pw.ms_official.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Muestra la página principal del administrador.
     *
     * @return El nombre de la vista para la página de inicio del administrador.
     */
    @Operation(summary = "Página de inicio del administrador", description = "Este endpoint muestra la página principal del administrador.")
    @GetMapping
    public String index() {
        return "admin/index";
    }

    /**
     * Muestra la lista de todos los usuarios.
     *
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista con la lista de usuarios.
     */
    @Operation(summary = "Ver lista de usuarios", description = "Este endpoint muestra la lista completa de usuarios en el sistema.")
    @GetMapping("/users")
    public String user(Model model) {
        return loadUserList(model, userService.getAllUsers()); // Carga la lista de usuarios
    }

    /**
     * Realiza una búsqueda de usuarios por nombre, correo electrónico o cédula.
     *
     * @param search El término de búsqueda (puede ser cédula, nombre o correo).
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista con los resultados de la búsqueda de usuarios.
     */
    @Operation(summary = "Buscar usuarios", description = "Este endpoint permite buscar usuarios por cédula, nombre o correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa, devuelve la lista de usuarios encontrados."),
            @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda no válido.")
    })
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam(required = false) String search, Model model) {
        List<UserEntity> users;

        if (search != null && !search.isEmpty()) {
            try {
                Long userCc = Long.parseLong(search);
                users = userService.findByCc(userCc);
            } catch (NumberFormatException e) {
                users = userService.findByNameOrEmailContaining(search);
            }
        } else {
            users = userService.getAllUsers();
        }

        return loadUserList(model, users);
    }

    /**
     * Carga y mapea la lista de usuarios al modelo.
     *
     * @param model El modelo que se pasa a la vista.
     * @param users La lista de usuarios a mostrar.
     * @return El nombre de la vista con los usuarios cargados.
     */
    private String loadUserList(Model model, List<UserEntity> users) {
        List<Map<String, Object>> userList = new ArrayList<>();
        for (UserEntity user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user", user);

            String roleNames = user.getRoles().stream()
                    .map(Rol::getName)
                    .collect(Collectors.joining(", "));

            userMap.put("roles", roleNames);
            userList.add(userMap);
        }
        model.addAttribute("userList", userList);
        return "admin/users";
    }

    /**
     * Muestra la lista de usuarios ordenados por el criterio especificado.
     *
     * @param sortBy El criterio de ordenación (por cédula, nombre o correo).
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista con la lista de usuarios ordenados.
     */
    @Operation(summary = "Ordenar usuarios", description = "Este endpoint permite ordenar la lista de usuarios por cédula, nombre o correo electrónico.")
    @GetMapping("/users/sort")
    public String sortUsers(@RequestParam(required = false) String sortBy, Model model) {
        List<UserEntity> users;

        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "cc":
                    users = userService.getAllUsersSortedByCc();
                    break;
                case "name":
                    users = userService.getAllUsersSortedByName();
                    break;
                case "email":
                    users = userService.getAllUsersSortedByEmail();
                    break;
                default:
                    users = userService.getAllUsers();
                    break;
            }
        } else {
            users = userService.getAllUsers();
        }

        return loadUserList(model, users);
    }

    /**
     * Muestra la lista de productos con opciones de búsqueda.
     *
     * @param searchTerm El término de búsqueda por nombre o descripción del producto.
     * @param searchById El ID del producto a buscar.
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista con la lista de productos filtrados.
     */
    @Operation(summary = "Ver productos", description = "Este endpoint muestra la lista de productos. Se puede buscar por nombre, descripción o ID del producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda y listado de productos exitoso."),
            @ApiResponse(responseCode = "400", description = "Error en la búsqueda de productos.")
    })
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "searchTerm", required = false, defaultValue = "") String searchTerm,
                               @RequestParam(value = "searchById", required = false) Long searchById,
                               Model model) {

        List<ProductEntity> products;

        if (searchById != null) {
            products = productService.getProductoById(searchById);
        } else {
            products = productService.searchByNameOrDescription(searchTerm);
        }

        model.addAttribute("productList", products);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("searchById", searchById);

        return "admin/products";
    }

    /**
     * Muestra el formulario para crear un nuevo producto.
     *
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista para el formulario de nuevo producto.
     */
    @Operation(summary = "Muestra el formulario para crear un nuevo producto", description = "Este endpoint muestra el formulario para agregar un nuevo producto a la tienda.")
    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "admin/new_product";
    }

    /**
     * Procesa el formulario para guardar un nuevo producto.
     *
     * @param product El objeto del producto que se debe guardar.
     * @param file El archivo de imagen para el producto.
     * @return Una redirección a la lista de productos.
     * @throws IOException Si ocurre un error al guardar el archivo de imagen.
     */
    @Operation(summary = "Guardar un nuevo producto", description = "Este endpoint procesa el formulario para guardar un nuevo producto y su imagen asociada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto guardado correctamente."),
            @ApiResponse(responseCode = "400", description = "Error al guardar el producto.")
    })
    @PostMapping("/products")
    public String saveProduct(@ModelAttribute ProductEntity product, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Product: " + product);
        if (file != null && !file.isEmpty()) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get("src/main/resources/static/img/" + filename);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl("/img/" + filename);
            System.out.println("Filename: " + file.getOriginalFilename());
        }
        productService.save(product);
        return "redirect:/admin/products";
    }

    /**
     * Muestra el formulario para editar un producto existente.
     *
     * @param id El ID del producto a editar.
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista para editar el producto.
     */
    @Operation(summary = "Muestra el formulario para editar un producto", description = "Este endpoint muestra el formulario para editar un producto existente.")
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/edit_product";
    }

    /**
     * Procesa la actualización de un producto existente.
     *
     * @param id El ID del producto a actualizar.
     * @param product El objeto del producto con los datos actualizados.
     * @param file El archivo de imagen para el producto (si se proporciona).
     * @return Una redirección a la lista de productos.
     * @throws IOException Si ocurre un error al guardar el archivo de imagen.
     */
    @Operation(summary = "Actualiza un producto existente", description = "Este endpoint procesa la actualización de un producto con un nuevo nombre, precio y/o imagen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente."),
            @ApiResponse(responseCode = "400", description = "Error al actualizar el producto.")
    })
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductEntity product, @RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("Actualizando producto con ID: " + id);
        System.out.println("Nombre del archivo: " + (file != null ? file.getOriginalFilename() : "Ninguno"));
        productService.updateProduct(id, product, file);
        return "redirect:/admin/products";
    }

    /**
     * Elimina un producto existente.
     *
     * @param id El ID del producto a eliminar.
     * @return Una redirección a la lista de productos.
     */
    @Operation(summary = "Elimina un producto", description = "Este endpoint elimina un producto de la tienda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente."),
            @ApiResponse(responseCode = "400", description = "Error al eliminar el producto.")
    })
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    /**
     * Muestra los pedidos administrados, con filtros opcionales.
     *
     * @param userCc El número de cédula del usuario para filtrar pedidos (opcional).
     * @param orderId El ID del pedido para obtener un pedido específico (opcional).
     * @param sort El criterio de ordenamiento (por ID o cédula) (opcional).
     * @param model El modelo que se pasa a la vista.
     * @return El nombre de la vista con la lista de pedidos.
     */
    @Operation(summary = "Muestra los pedidos administrados", description = "Este endpoint permite visualizar la lista de pedidos, con la opción de filtrarlos por cédula de usuario, ID de pedido y ordenarlos por diferentes criterios.")
    @GetMapping("/orders")
    public String adminOrders(@RequestParam(required = false) Long userCc,
                              @RequestParam(required = false) Long orderId,
                              @RequestParam(required = false) String sort,
                              Model model) {
        List<OrderEntity> orders = new ArrayList<>();

        if (userCc != null) {
            orders = orderService.findByUserCc(userCc);
        } else if (orderId != null) {
            Optional<OrderEntity> orderOptional = orderService.findById(orderId);
            if (orderOptional.isPresent()) {
                orders = Collections.singletonList(orderOptional.get());
            } else {
                model.addAttribute("message", "No se encontró el pedido con ID: " + orderId);
            }
        } else {
            orders = orderService.getAllOrders();
        }

        if ("id".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(OrderEntity::getId));
        } else if ("cc".equalsIgnoreCase(sort)) {
            orders.sort(Comparator.comparing(order -> order.getUser().getCc()));
        }

        model.addAttribute("orders", orders);
        return "/admin/orders";
    }

    /**
     * Muestra el formulario para generar un reporte de ventas.
     *
     * @return El nombre de la vista para mostrar el formulario de reporte.
     */
    @Operation(summary = "Muestra el formulario para generar un reporte de ventas", description = "Este endpoint permite mostrar el formulario para generar un reporte de ventas basado en fechas específicas.")
    @GetMapping("/report")
    public String showReportForm() {
        return "/admin/report";
    }

    /**
     * Genera un reporte de ventas para un rango de fechas especificado.
     *
     * @param dateType El tipo de filtro de fecha (día, mes o año).
     * @param specificDate La fecha específica para el filtro de día (opcional).
     * @param specificMonth El mes específico para el filtro de mes (opcional).
     * @param specificYear El año específico para el filtro de año (opcional).
     * @param model El modelo que se pasa a la vista con los datos del reporte.
     * @return El nombre de la vista con el reporte generado.
     */
    @Operation(summary = "Genera un reporte de ventas", description = "Este endpoint permite generar un reporte de ventas basado en un rango de fechas (día, mes, año).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente."),
            @ApiResponse(responseCode = "400", description = "Fecha no válida o tipo de fecha incorrecto.")
    })
    @GetMapping("/generate")
    public String generateReport(
            @RequestParam("dateType") String dateType,
            @RequestParam(value = "specificDate", required = false) String specificDate,
            @RequestParam(value = "specificMonth", required = false) String specificMonth,
            @RequestParam(value = "specificYear", required = false) String specificYear,
            Model model) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        switch (dateType) {
            case "day":
                if (specificDate == null) {
                    throw new IllegalArgumentException("Debe proporcionar una fecha.");
                }
                startDate = LocalDate.parse(specificDate).atStartOfDay();
                endDate = startDate.plusDays(1).minusSeconds(1);
                break;
            case "month":
                if (specificMonth == null) {
                    throw new IllegalArgumentException("Debe proporcionar un mes.");
                }
                YearMonth month = YearMonth.parse(specificMonth);
                startDate = month.atDay(1).atStartOfDay();
                endDate = month.atEndOfMonth().atTime(23, 59, 59);
                break;
            case "year":
                if (specificYear == null) {
                    throw new IllegalArgumentException("Debe proporcionar un año.");
                }
                int year = Integer.parseInt(specificYear);
                startDate = LocalDate.of(year, 1, 1).atStartOfDay();
                endDate = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
                break;
            default:
                throw new IllegalArgumentException("Filtro inválido");
        }

        List<Object[]> reportData = orderRepository.findSalesReport(startDate, endDate);

        BigDecimal totalSales = reportData.stream()
                .map(data -> (BigDecimal) data[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("reportData", reportData);
        model.addAttribute("totalSales", totalSales);

        return "/admin/sales-report";
    }

    /**
     * Actualiza el estado de un pedido en el sistema.
     *
     * @param orderId El ID del pedido cuyo estado se actualizará.
     * @param status El nuevo estado del pedido.
     * @return La redirección a la lista de pedidos con un mensaje de éxito o error.
     */
    @Operation(summary = "Actualiza el estado de un pedido", description = "Este endpoint permite actualizar el estado de un pedido existente en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado correctamente."),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping("/order/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        try {
            // Obtener la orden por su ID
            Optional<OrderEntity> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isEmpty()) {
                return "redirect:/admin/orders?error=notfound";
            }

            OrderEntity order = orderOptional.get();
            String oldStatus = order.getStatus();

            order.setStatus(status);
            orderRepository.save(order);

            if ("REFUND".equals(status) && !"REFUND".equals(oldStatus)) {
                for (OrderDetailsEntity orderDetail : order.getOrderDetails()) {
                    ProductEntity product = orderDetail.getProduct();
                    int quantity = orderDetail.getQuantity();

                    product.setStock(product.getStock() + quantity);

                    productRepository.save(product);
                }
            }

            return "redirect:/admin/orders?success=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/orders?error=true";
        }
    }


}

