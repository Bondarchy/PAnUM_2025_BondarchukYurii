package com.example.lab6;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final double SET_DISCOUNT = 4.00;

    private DatabaseHelper databaseHelper;
    private List<DatabaseHelper.Product> currentProducts = new ArrayList<>();
    private final Map<Long, CartItem> cart = new LinkedHashMap<>();

    private ScrollView mainScroll;

    private TextView cartBadge;
    private Button btnDrinks;
    private Button btnSnacks;
    private Button btnLocation;

    private LinearLayout productSection;
    private LinearLayout locationSection;
    private LinearLayout historySection;
    private LinearLayout orderSection;

    private TextView categoryTitle;
    private TextView productIcon;
    private Spinner productSpinner;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private TextView quantityText;

    private Button btnMinus;
    private Button btnPlus;
    private Button btnAddToCart;

    private TextView locationName;
    private TextView locationAddress;
    private TextView locationHours;

    private TextView cartText;
    private TextView discountText;
    private TextView totalText;
    private TextView orderStatusText;
    private TextView historyText;

    private EditText emailInput;

    private Button btnPlaceOrder;
    private Button btnSendEmail;
    private Button btnClearCart;
    private Button btnHistory;

    private DecimalFormat priceFormat;

    private int selectedQuantity = 1;
    private boolean orderConfirmed = false;
    private long confirmedOrderId = -1;
    private String confirmedReceipt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DecimalFormatSymbols symbols =
                DecimalFormatSymbols.getInstance(new Locale("pl", "PL"));
        priceFormat = new DecimalFormat("0.00", symbols);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        initializeListeners();

        showProducts(DatabaseHelper.CATEGORY_DRINKS, "Menu napojów");
        updateCartView();
    }

    private void initializeViews() {
        mainScroll = findViewById(R.id.mainScroll);

        cartBadge = findViewById(R.id.cartBadge);
        btnDrinks = findViewById(R.id.btnDrinks);
        btnSnacks = findViewById(R.id.btnSnacks);
        btnLocation = findViewById(R.id.btnLocation);

        productSection = findViewById(R.id.productSection);
        locationSection = findViewById(R.id.locationSection);
        historySection = findViewById(R.id.historySection);
        orderSection = findViewById(R.id.orderSection);

        categoryTitle = findViewById(R.id.categoryTitle);
        productIcon = findViewById(R.id.productIcon);
        productSpinner = findViewById(R.id.productSpinner);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        quantityText = findViewById(R.id.quantityText);

        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        locationName = findViewById(R.id.locationName);
        locationAddress = findViewById(R.id.locationAddress);
        locationHours = findViewById(R.id.locationHours);

        cartText = findViewById(R.id.cartText);
        discountText = findViewById(R.id.discountText);
        totalText = findViewById(R.id.totalText);
        orderStatusText = findViewById(R.id.orderStatusText);
        historyText = findViewById(R.id.historyText);

        emailInput = findViewById(R.id.emailInput);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnHistory = findViewById(R.id.btnHistory);

        btnSendEmail.setEnabled(false);
    }

    private void initializeListeners() {
        btnDrinks.setOnClickListener(v ->
                showProducts(DatabaseHelper.CATEGORY_DRINKS, "Menu napojów"));

        btnSnacks.setOnClickListener(v ->
                showProducts(DatabaseHelper.CATEGORY_SNACKS, "Menu przekąsek"));

        btnLocation.setOnClickListener(v -> showLocation());

        cartBadge.setOnClickListener(v ->
                mainScroll.smoothScrollTo(0, orderSection.getTop()));

        btnMinus.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                quantityText.setText(String.valueOf(selectedQuantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (selectedQuantity < 99) {
                selectedQuantity++;
                quantityText.setText(String.valueOf(selectedQuantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> addSelectedProductToCart());

        btnClearCart.setOnClickListener(v -> clearCart());

        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        btnSendEmail.setOnClickListener(v -> sendReceiptByEmail());

        btnHistory.setOnClickListener(v -> toggleHistory());

        productSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        showSelectedProduct(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void showProducts(String category, String title) {
        productSection.setVisibility(View.VISIBLE);
        locationSection.setVisibility(View.GONE);

        categoryTitle.setText(title);

        currentProducts = databaseHelper.getProductsByCategory(category);

        List<String> productNames = new ArrayList<>();

        for (DatabaseHelper.Product product : currentProducts) {
            productNames.add(product.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                productNames
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        productSpinner.setAdapter(adapter);

        selectedQuantity = 1;
        quantityText.setText("1");

        if (!currentProducts.isEmpty()) {
            productSpinner.setSelection(0);
            showSelectedProduct(0);
        }
    }

    private void showSelectedProduct(int position) {
        if (position < 0 || position >= currentProducts.size()) {
            return;
        }

        DatabaseHelper.Product product = currentProducts.get(position);

        productIcon.setText(product.getIcon());
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());
        productPrice.setText("Cena: " + formatPrice(product.getPrice()));

        selectedQuantity = 1;
        quantityText.setText("1");
    }

    private void showLocation() {
        productSection.setVisibility(View.GONE);
        locationSection.setVisibility(View.VISIBLE);

        DatabaseHelper.Location location = databaseHelper.getLocation();

        if (location != null) {
            locationName.setText(location.getCafeName());
            locationAddress.setText(location.getAddress());
            locationHours.setText(location.getOpeningHours());
        }
    }

    private void addSelectedProductToCart() {
        int selectedPosition = productSpinner.getSelectedItemPosition();

        if (selectedPosition < 0 || selectedPosition >= currentProducts.size()) {
            return;
        }

        DatabaseHelper.Product product = currentProducts.get(selectedPosition);
        CartItem cartItem = cart.get(product.getId());

        if (cartItem == null) {
            cart.put(product.getId(), new CartItem(product, selectedQuantity));
        } else {
            cartItem.quantity += selectedQuantity;
        }

        invalidateConfirmation();

        selectedQuantity = 1;
        quantityText.setText("1");

        updateCartView();

        Toast.makeText(
                this,
                "Dodano: " + product.getName(),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void clearCart() {
        cart.clear();
        invalidateConfirmation();
        updateCartView();

        Toast.makeText(
                this,
                "Wyczyszczono zamówienie",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void invalidateConfirmation() {
        orderConfirmed = false;
        confirmedOrderId = -1;
        confirmedReceipt = "";

        btnSendEmail.setEnabled(false);
        orderStatusText.setVisibility(View.GONE);
    }

    private void updateCartView() {
        int productsCount = 0;

        for (CartItem item : cart.values()) {
            productsCount += item.quantity;
        }

        cartBadge.setText("🛒  " + productsCount);

        if (cart.isEmpty()) {
            cartText.setText("Koszyk jest pusty.");
            discountText.setVisibility(View.GONE);
            totalText.setText("Suma: 0,00 zł");
            return;
        }

        StringBuilder receipt = new StringBuilder();
        int number = 1;

        for (CartItem item : cart.values()) {
            double productTotal =
                    item.product.getPrice() * item.quantity;

            receipt.append(number)
                    .append(". ")
                    .append(item.product.getName())
                    .append("  x")
                    .append(item.quantity)
                    .append("  =  ")
                    .append(formatPrice(productTotal))
                    .append("\n");

            number++;
        }

        cartText.setText(receipt.toString().trim());

        double discount = calculateDiscount();

        if (discount > 0) {
            discountText.setVisibility(View.VISIBLE);
            discountText.setText(
                    "Promocja: Cappuccino + Sernik x"
                            + getSetCount()
                            + "   −"
                            + formatPrice(discount)
            );
        } else {
            discountText.setVisibility(View.GONE);
        }

        totalText.setText("Suma: " + formatPrice(calculateTotal()));
    }

    private double calculateSubtotal() {
        double subtotal = 0;

        for (CartItem item : cart.values()) {
            subtotal += item.product.getPrice() * item.quantity;
        }

        return subtotal;
    }

    private int getSetCount() {
        int cappuccinoCount = 0;
        int cheesecakeCount = 0;

        for (CartItem item : cart.values()) {
            if (item.product.getName().equals("Cappuccino")) {
                cappuccinoCount = item.quantity;
            }

            if (item.product.getName().equals("Sernik")) {
                cheesecakeCount = item.quantity;
            }
        }

        return Math.min(cappuccinoCount, cheesecakeCount);
    }

    private double calculateDiscount() {
        return getSetCount() * SET_DISCOUNT;
    }

    private double calculateTotal() {
        return calculateSubtotal() - calculateDiscount();
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            Toast.makeText(
                    this,
                    "Najpierw dodaj produkty do koszyka",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (orderConfirmed) {
            Toast.makeText(
                    this,
                    "To zamówienie zostało już zapisane",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String email = emailInput.getText().toString().trim();

        if (!email.isEmpty()
                && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                    this,
                    "Wprowadź poprawny adres e-mail",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        List<DatabaseHelper.OrderItem> itemsToSave = new ArrayList<>();

        for (CartItem item : cart.values()) {
            itemsToSave.add(new DatabaseHelper.OrderItem(
                    item.product.getName(),
                    item.quantity,
                    item.product.getPrice(),
                    item.product.getPrice() * item.quantity
            ));
        }

        confirmedOrderId = databaseHelper.saveOrder(
                itemsToSave,
                calculateSubtotal(),
                calculateDiscount(),
                calculateTotal(),
                email
        );

        orderConfirmed = true;
        confirmedReceipt = buildReceiptForEmail(confirmedOrderId);

        btnSendEmail.setEnabled(true);

        orderStatusText.setVisibility(View.VISIBLE);
        orderStatusText.setText(
                "✓ Zamówienie nr "
                        + confirmedOrderId
                        + " zostało przyjęte!\n"
                        + "Czas oczekiwania: około 10 minut."
        );

        new AlertDialog.Builder(this)
                .setTitle("Zamówienie przyjęte!")
                .setMessage(
                        "Numer zamówienia: "
                                + confirmedOrderId
                                + "\nDo zapłaty: "
                                + formatPrice(calculateTotal())
                                + "\nCzas oczekiwania: około 10 minut."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private String buildReceiptForEmail(long orderId) {
        StringBuilder receipt = new StringBuilder();

        receipt.append("CAFE AROMA\n")
                .append("Rachunek – zamówienie nr ")
                .append(orderId)
                .append("\n\n");

        for (CartItem item : cart.values()) {
            receipt.append(item.product.getName())
                    .append(" x")
                    .append(item.quantity)
                    .append(" = ")
                    .append(formatPrice(
                            item.product.getPrice() * item.quantity))
                    .append("\n");
        }

        if (calculateDiscount() > 0) {
            receipt.append("Rabat – zestaw dnia: -")
                    .append(formatPrice(calculateDiscount()))
                    .append("\n");
        }

        receipt.append("\nSUMA: ")
                .append(formatPrice(calculateTotal()))
                .append("\n\nDziękujemy za zamówienie!");

        return receipt.toString();
    }

    private void sendReceiptByEmail() {
        if (!orderConfirmed) {
            Toast.makeText(
                    this,
                    "Najpierw złóż zamówienie",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                    this,
                    "Wprowadź poprawny adres e-mail",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String subject =
                "Rachunek Cafe Aroma – zamówienie nr " + confirmedOrderId;

        Uri mailUri = Uri.parse(
                "mailto:" + Uri.encode(email)
                        + "?subject=" + Uri.encode(subject)
                        + "&body=" + Uri.encode(confirmedReceipt)
        );

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailUri);

        try {
            startActivity(Intent.createChooser(
                    emailIntent,
                    "Wyślij rachunek"
            ));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    this,
                    "Brak aplikacji pocztowej na urządzeniu",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void toggleHistory() {
        if (historySection.getVisibility() == View.VISIBLE) {
            historySection.setVisibility(View.GONE);
            btnHistory.setText("Historia zamówień");
        } else {
            historyText.setText(databaseHelper.getOrderHistoryText());
            historySection.setVisibility(View.VISIBLE);
            btnHistory.setText("Ukryj historię");

            mainScroll.post(() ->
                    mainScroll.smoothScrollTo(0, historySection.getBottom()));
        }
    }

    private String formatPrice(double value) {
        return priceFormat.format(value) + " zł";
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

    private static class CartItem {

        private final DatabaseHelper.Product product;
        private int quantity;

        private CartItem(DatabaseHelper.Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }
}