import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// --- DARK THEME STYLING ---
class DarkTheme {
    public static final Color BG_MAIN     = new Color(30, 30, 30);     
    public static final Color BG_PANEL    = new Color(40, 40, 40);     
    public static final Color BG_SIDEBAR  = new Color(20, 20, 20);     
    public static final Color TEXT_LIGHT  = new Color(245, 245, 245); 
    public static final Color TEXT_GRAY   = new Color(170, 170, 170);
    public static final Color TEXT_ACCENT = new Color(64, 180, 255); 
    public static final Color TEXT_GREEN  = new Color(100, 255, 140);
    public static final Color ACCENT_PURPLE = new Color(180, 160, 255);
    public static final Color BORDER_COLOR = new Color(70, 70, 70); 
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28); 
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 14); 

    public static JLabel createHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(0, 0, 25, 0));
        return lbl;
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(new Color(240, 240, 240));
        btn.setForeground(Color.BLACK); 
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1), 
            new EmptyBorder(10, 25, 10, 25)
        ));
        return btn;
    }
}

// --- CUSTOM SCROLLBAR ---
class DarkScrollBarUI extends BasicScrollBarUI {
    @Override protected void configureScrollBarColors() {
        this.thumbColor = new Color(80, 80, 80); 
        this.trackColor = DarkTheme.BG_MAIN;     
    }
    @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
    @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
    private JButton createZeroButton() {
        JButton j = new JButton();
        j.setPreferredSize(new Dimension(0, 0));
        return j;
    }
    @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(trackColor); g.fillRect(r.x, r.y, r.width, r.height);
    }
    @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.dispose();
    }
}

// --- DATA MODEL ---
class CurrencyData {
    String code, name, region, symbol;
    double rateToUSD;
    String[] denominations;
    String centralBank, material, nickname, mainUnit, fractionalUnit, foundedYear;
    Color themeColor;      
    double coinThreshold; 
    int strengthRank; 

    public CurrencyData(String code, String name, String region, String symbol, double rate, String[] dens,
                        String bank, String mat, String nick, String unit, String frac, String year, Color color, double coinLimit) {
        this.code = code; this.name = name; this.region = region; this.symbol = symbol;
        this.rateToUSD = rate; this.denominations = dens;
        this.centralBank = bank; this.material = mat; this.nickname = nick;
        this.mainUnit = unit; this.fractionalUnit = frac; this.foundedYear = year;
        this.themeColor = color; this.coinThreshold = coinLimit;
    }
}

// --- LORE ENGINE ---
class LoreGenerator {
    public static String generateHistory(CurrencyData c, String denom) {
        double val = Double.parseDouble(denom);
        String type = (val <= c.coinThreshold) ? "coin" : "banknote";
        return "1. HISTORICAL ORIGINS\nThe " + c.name + " (" + c.code + "), bearing the symbol '" + c.symbol + "', traces its modern lineage back to " + c.foundedYear + ". As the official legal tender of " + c.region + ", it represents centuries of economic evolution.\n\n" +
            "2. CENTRAL BANKING AUTHORITY\nIssuance is strictly controlled by the " + c.centralBank + ". This institution manages monetary policy to ensure price stability. The release of the " + denom + " " + type + " is a calculated decision based on economic liquidity.\n\n" +
            "3. MATERIAL COMPOSITION\nThe physical currency is a marvel of engineering. Constructed primarily from " + c.material + ", this " + type + " is designed to withstand heavy circulation.\n\n" +
            "4. ICONOGRAPHY & DESIGN\nThe design of the " + c.code + " reflects the soul of the nation. The " + denom + " denomination features intricate artwork, often depicting national heroes, wildlife, or architecture.\n\n" +
            "5. ANTI-COUNTERFEITING MEASURES\nTo protect against forgery, the " + c.centralBank + " integrates state-of-the-art security features like holograms, micro-printing, and specialized inks.\n\n" +
            "6. ROLE OF THE " + denom + "\nThe " + denom + " unit holds a specific place in the transaction hierarchy. " + (val > 50 ? "As a higher denomination, it is a store of value." : "As a lower denomination, it facilitates daily commerce.") + "\n\n" +
            "7. ECONOMIC STANDING\nIn the forex market, the " + c.code + " trades at approximately " + c.rateToUSD + " per USD. This reflects the region's purchasing power parity.\n\n" +
            "8. FRACTIONAL SYSTEM\nThe currency is decimalized, subdivided into the " + c.fractionalUnit + ". These units are essential for precise pricing psychology.\n\n" +
            "9. CULTURAL SIGNIFICANCE\nLocals often refer to their money as '" + c.nickname + "'. It appears in art and media as a symbol of the nation's identity.\n\n" +
            "10. FUTURE OUTLOOK\nAs the world moves toward cashless payments, the physical " + denom + " " + c.code + " remains vital for anonymity and emergency resilience.";
    }

    public static String generateSummary(CurrencyData c, String denom) {
        return "1.  Name: " + c.name + "\n" +
            "2.  ISO Code: " + c.code + "\n" +
            "3.  Global Rank: #" + c.strengthRank + " (Strength)\n" +
            "4.  Region: " + c.region + "\n" +
            "5.  Issuer: " + c.centralBank + "\n" +
            "6.  Main Unit: " + c.mainUnit + "\n" +
            "7.  Sub-unit: " + c.fractionalUnit + "\n" +
            "8.  Material: " + c.material + "\n" +
            "9.  Nickname: \"" + c.nickname + "\"\n" +
            "10. USD Rate: ~" + c.rateToUSD;
    }
}

// --- UI COMPONENTS ---
class FilterButton extends JToggleButton {
    private Color hoverColor = new Color(60, 60, 60);
    private Color selectedColor = DarkTheme.TEXT_ACCENT;
    private boolean isHovering = false;

    public FilterButton(String text) {
        super(text);
        setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
        setForeground(DarkTheme.TEXT_GRAY); setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { isHovering = true; repaint(); }
            public void mouseExited(MouseEvent e) { isHovering = false; repaint(); }
        });
        addItemListener(e -> { setForeground(isSelected() ? Color.BLACK : DarkTheme.TEXT_GRAY); repaint(); });
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected()) { g2.setColor(selectedColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); }
        else if (isHovering) { g2.setColor(hoverColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); }
        else { g2.setColor(DarkTheme.BG_PANEL); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); g2.setColor(DarkTheme.BORDER_COLOR); g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30); }
        super.paintComponent(g2); g2.dispose();
    }
}

class CurrencyIcon implements Icon {
    private final CurrencyData data;
    private final String value;
    private final boolean isCoin;
    public CurrencyIcon(CurrencyData data, String value) {
        this.data = data; this.value = value;
        try { this.isCoin = Double.parseDouble(value) <= data.coinThreshold; } catch(Exception e) { throw new RuntimeException(value); }
    }
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isCoin) {
            int d = 46, cx = x+(100-d)/2, cy = y+(60-d)/2;
            g2.setColor(new Color(218, 165, 32)); 
            if(data.code.matches("JPY|CNY|USD|KRW")) g2.setColor(new Color(192, 192, 192));
            g2.fillOval(cx, cy, d, d);
            g2.setColor(data.themeColor.brighter()); g2.fillOval(cx+5, cy+5, d-10, d-10);
            g2.setColor(Color.BLACK); g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String t = value; FontMetrics fm = g2.getFontMetrics();
            g2.drawString(t, cx+(d-fm.stringWidth(t))/2, cy+(d+fm.getAscent())/2-2);
        } else {
            int bw=85, bh=45, bx=x+(100-bw)/2, by=y+(60-bh)/2;
            g2.setColor(data.themeColor); g2.fillRoundRect(bx, by, bw, bh, 6, 6);
            g2.setColor(new Color(0,0,0,40)); g2.drawRoundRect(bx+3, by+3, bw-6, bh-6, 4, 4);
            g2.setColor(Color.BLACK); g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String t = value; FontMetrics fm = g2.getFontMetrics();
            g2.drawString(t, bx+(bw-fm.stringWidth(t))/2, by+(bh+fm.getAscent())/2-2);
        }
        g2.dispose();
    }
    @Override public int getIconWidth() { return 100; }
    @Override public int getIconHeight() { return 60; }
}

// --- MAIN APP ---
public class GlobalCurrencyApp extends JFrame {
    private JPanel cards, listPanel, billsGridPanel;
    private CardLayout cardLayout;
    private List<CurrencyData> allCurrencies;
    private CurrencyData selectedCurrency;
    private String selectedDenom, currentRegionFilter = "All";
    private JTextField txtSearch;
    private JTextArea txtFacts, txtFinalSummary;
    private JLabel lblResultHeader, lblConversionResult, lblStep2Header;
    private JButton nav1, nav2, nav3;
    private String currentView = "Page1"; 

    public GlobalCurrencyApp() {
        initializeGlobalData();
        calculateGlobalRanks(); 

        setTitle("Global Currency Archive | 180 Currencies");
        setSize(1350, 900); setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(DarkTheme.BG_MAIN);
        
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setBackground(DarkTheme.BG_MAIN);
        cards.setBorder(new EmptyBorder(20, 30, 20, 30));

        cards.add(createPage1(), "Page1");
        cards.add(createPage2(), "Page2");
        cards.add(createPage3(), "Page3"); 

        add(createSidebar(), BorderLayout.WEST);
        add(cards, BorderLayout.CENTER);
        
        updateNavigationState("Page1");
        filterCurrencyList("");
    }
    
    private void calculateGlobalRanks() {
        allCurrencies.sort((c1, c2) -> Double.compare(c2.rateToUSD, c1.rateToUSD));
        for(int i=0; i<allCurrencies.size(); i++) allCurrencies.get(i).strengthRank = i + 1;
        allCurrencies.sort(Comparator.comparing(c -> c.code));
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DarkTheme.BG_SIDEBAR);
        panel.setPreferredSize(new Dimension(260, 800));
        panel.setBorder(new MatteBorder(0,0,0,1, DarkTheme.BORDER_COLOR));

        JLabel title = new JLabel("<html>GLOBAL<br><font color='#40B4FF'>TREASURY</font></html>");
        title.setFont(DarkTheme.FONT_HEADER.deriveFont(24f));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(40, 30, 50, 20));
        panel.add(title);
        
        nav1 = createNavLink("Select Currency", "Page1"); 
        nav2 = createNavLink("Treasury", "Page2");        
        nav3 = createNavLink("Analysis", "Page3");        
        panel.add(nav1); panel.add(nav2); panel.add(nav3);
        panel.add(Box.createVerticalGlue()); 
        
        JLabel footer = new JLabel("Database: " + allCurrencies.size() + " Currencies");
        footer.setForeground(Color.GRAY);
        footer.setBorder(new EmptyBorder(0,30,20,0));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(footer);
        return panel;
    }
    
    private JButton createNavLink(String text, String viewName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(260, 55));
        btn.setPreferredSize(new Dimension(260, 55));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(0, 30, 0, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true); 
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { if (!currentView.equals(viewName)) { btn.setBackground(new Color(40,40,40)); btn.setForeground(Color.WHITE); } }
            public void mouseExited(MouseEvent evt) { if (!currentView.equals(viewName)) { btn.setBackground(DarkTheme.BG_SIDEBAR); btn.setForeground(DarkTheme.TEXT_GRAY); } }
        });
        btn.addActionListener(e -> {
            if ((viewName.equals("Page3") || viewName.equals("Page2")) && selectedCurrency == null) return;
            cardLayout.show(cards, viewName);
            updateNavigationState(viewName);
        });
        return btn;
    }

    private void updateNavigationState(String activeView) {
        this.currentView = activeView;
        JButton[] navs = {nav1, nav2, nav3};
        for (JButton btn : navs) {
            btn.setBackground(DarkTheme.BG_SIDEBAR);
            btn.setForeground(DarkTheme.TEXT_GRAY);
            btn.setBorder(new EmptyBorder(0, 30, 0, 0));
        }
        JButton active = null;
        switch (activeView) {
            case "Page1": active = nav1; break;
            case "Page2": active = nav2; break;
            case "Page3": active = nav3; break;
        }
        if (active != null) {
            active.setBackground(new Color(35, 35, 35));
            active.setForeground(DarkTheme.TEXT_ACCENT);
            active.setBorder(new CompoundBorder(new MatteBorder(0, 4, 0, 0, DarkTheme.TEXT_ACCENT), new EmptyBorder(0, 26, 0, 0)));
        }
    }
    
    // --- PAGES ---
    private JPanel createPage1() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(DarkTheme.BG_MAIN);
        
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(DarkTheme.BG_MAIN);
        top.add(DarkTheme.createHeader("Select Region & Currency"), BorderLayout.NORTH);
        
        JPanel controls = new JPanel(new BorderLayout(20, 0));
        controls.setBackground(DarkTheme.BG_MAIN);
        
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Search country or code..."); 
        txtSearch.setFont(DarkTheme.FONT_LABEL);
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setBackground(DarkTheme.BG_PANEL);
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setCaretColor(DarkTheme.TEXT_ACCENT);
        txtSearch.setBorder(new LineBorder(DarkTheme.BORDER_COLOR));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCurrencyList(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { filterCurrencyList(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { filterCurrencyList(txtSearch.getText()); }
        });
        controls.add(txtSearch, BorderLayout.WEST);

        JPanel pillPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pillPanel.setBackground(DarkTheme.BG_MAIN);
        String[] regions = {"All", "Asia", "Europe", "North America", "South America", "Africa", "Middle East", "Oceania", "Caribbean"};
        ButtonGroup bg = new ButtonGroup();
        for(String r : regions) {
            FilterButton fb = new FilterButton(r);
            fb.setPreferredSize(new Dimension(r.length()*8 + 35, 40));
            fb.addActionListener(e -> { currentRegionFilter = r; filterCurrencyList(txtSearch.getText()); });
            if(r.equals("All")) fb.setSelected(true);
            bg.add(fb); pillPanel.add(fb);
        }
        JScrollPane pillScroll = new JScrollPane(pillPanel);
        pillScroll.setBorder(null);
        pillScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pillScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pillScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8)); 
        pillScroll.getHorizontalScrollBar().setUI(new DarkScrollBarUI()); 
        pillScroll.getViewport().setBackground(DarkTheme.BG_MAIN);
        pillScroll.setPreferredSize(new Dimension(800, 60)); 
        
        controls.add(pillScroll, BorderLayout.CENTER);
        top.add(controls, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        listPanel = new JPanel(new GridLayout(0, 4, 15, 15)); 
        listPanel.setBackground(DarkTheme.BG_MAIN);
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(DarkTheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void filterCurrencyList(String query) {
        listPanel.removeAll();
        String q = query.toLowerCase();
        List<CurrencyData> filtered = allCurrencies.stream()
            .filter(c -> (currentRegionFilter.equals("All") || c.region.equals(currentRegionFilter)))
            .filter(c -> c.code.toLowerCase().contains(q) || c.name.toLowerCase().contains(q))
            .collect(Collectors.toList());
            
        for(CurrencyData c : filtered) {
            JButton card = new JButton();
            card.setLayout(new BorderLayout());
            card.setBackground(DarkTheme.BG_PANEL);
            card.setBorder(new LineBorder(DarkTheme.BORDER_COLOR));
            card.setPreferredSize(new Dimension(220, 100)); 
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.setFocusPainted(false);
            card.setContentAreaFilled(false);
            card.setOpaque(true); 
            
            JLabel codeLbl = new JLabel(c.code + " " + c.symbol);
            codeLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
            codeLbl.setForeground(Color.WHITE);
            codeLbl.setBorder(new EmptyBorder(10, 12, 0, 0));
            
            JLabel nameLbl = new JLabel(c.name);
            nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            nameLbl.setForeground(DarkTheme.TEXT_GRAY);
            nameLbl.setBorder(new EmptyBorder(0, 12, 10, 0));
            
            JPanel strip = new JPanel();
            strip.setBackground(c.themeColor);
            strip.setPreferredSize(new Dimension(5, 100));
            card.add(strip, BorderLayout.WEST);
            
            JPanel txtPanel = new JPanel(new GridLayout(2,1));
            txtPanel.setOpaque(false);
            txtPanel.add(codeLbl);
            txtPanel.add(nameLbl);
            card.add(txtPanel, BorderLayout.CENTER);

            card.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { 
                    card.setBackground(new Color(230, 230, 230)); 
                    codeLbl.setForeground(Color.BLACK); 
                    nameLbl.setForeground(Color.DARK_GRAY); 
                    card.setBorder(new LineBorder(c.themeColor, 2));
                }
                public void mouseExited(MouseEvent e) { 
                    card.setBackground(DarkTheme.BG_PANEL); 
                    codeLbl.setForeground(Color.WHITE); 
                    nameLbl.setForeground(DarkTheme.TEXT_GRAY);
                    card.setBorder(new LineBorder(DarkTheme.BORDER_COLOR));
                }
            });
            card.addActionListener(e -> { selectedCurrency = c; loadBills(); cardLayout.show(cards, "Page2"); updateNavigationState("Page2"); });
            listPanel.add(card);
        }
        listPanel.revalidate(); listPanel.repaint();
    }

    private JPanel createPage2() {
        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(DarkTheme.BG_MAIN);
        lblStep2Header = DarkTheme.createHeader("Select Denomination");
        panel.add(lblStep2Header, BorderLayout.NORTH);

        billsGridPanel = new JPanel(new GridLayout(0, 5, 20, 20)); 
        billsGridPanel.setBackground(DarkTheme.BG_MAIN);
        JScrollPane scroll = new JScrollPane(billsGridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(DarkTheme.BG_MAIN); 
        scroll.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        
        panel.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(DarkTheme.BG_MAIN);
        JButton btnBack = DarkTheme.createButton("← RETURN");
        btnBack.addActionListener(e -> { cardLayout.show(cards, "Page1"); updateNavigationState("Page1"); });
        south.add(btnBack);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }
    
    private void loadBills() {
        billsGridPanel.removeAll();
        lblStep2Header.setText(selectedCurrency.name + " (" + selectedCurrency.code + ")");

        for(String den : selectedCurrency.denominations) {
            CurrencyIcon icon = new CurrencyIcon(selectedCurrency, den);
            JButton b = new JButton(icon);
            String unitName = selectedCurrency.mainUnit;
            String amount = den;
            try {
                double val = Double.parseDouble(den);
                if (val < 1.0 && val > 0) { amount = String.valueOf((int)(val * 100)); unitName = selectedCurrency.fractionalUnit; }
                else if (val > 1.0) { unitName += "s"; }
            } catch(Exception e){}
            
            b.setText("<html><center><font size='5'>" + amount + "</font><br><font size='3'>" + unitName + "</font></center></html>"); 
            b.setVerticalTextPosition(SwingConstants.BOTTOM); b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setBackground(DarkTheme.BG_PANEL); b.setForeground(Color.WHITE); 
            b.setFocusPainted(false); b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setBorder(new LineBorder(DarkTheme.BORDER_COLOR)); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setContentAreaFilled(false); b.setOpaque(true);
            b.setPreferredSize(new Dimension(100, 120));

            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(new Color(230, 230, 230)); b.setForeground(Color.BLACK); b.setBorder(new LineBorder(selectedCurrency.themeColor, 2)); }
                public void mouseExited(MouseEvent e) { b.setBackground(DarkTheme.BG_PANEL); b.setForeground(Color.WHITE); b.setBorder(new LineBorder(DarkTheme.BORDER_COLOR)); }
            });
            b.addActionListener(e -> { selectedDenom = den; loadAnalysis(); cardLayout.show(cards, "Page3"); updateNavigationState("Page3"); });
            billsGridPanel.add(b);
        }
        billsGridPanel.revalidate(); billsGridPanel.repaint();
    }

    private JPanel createPage3() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(DarkTheme.BG_MAIN);
        lblResultHeader = DarkTheme.createHeader("Analysis");
        panel.add(lblResultHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(850); 
        splitPane.setBorder(null);
        splitPane.setBackground(DarkTheme.BG_MAIN);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(DarkTheme.BG_PANEL);
        leftPanel.setBorder(new LineBorder(DarkTheme.BORDER_COLOR));

        txtFacts = new JTextArea();
        txtFacts.setEditable(false);
        txtFacts.setFont(new Font("Georgia", Font.PLAIN, 16)); 
        txtFacts.setBackground(DarkTheme.BG_PANEL);
        txtFacts.setForeground(DarkTheme.TEXT_LIGHT);
        txtFacts.setMargin(new Insets(30, 30, 30, 30));
        txtFacts.setLineWrap(true);
        txtFacts.setWrapStyleWord(true);
        JScrollPane scrollFacts = new JScrollPane(txtFacts);
        scrollFacts.setBorder(null);
        scrollFacts.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        leftPanel.add(scrollFacts, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        rightPanel.setBackground(DarkTheme.BG_MAIN);
        
        JPanel sumPanel = new JPanel(new BorderLayout());
        sumPanel.setBackground(DarkTheme.BG_PANEL);
        sumPanel.setBorder(new TitledBorder(new LineBorder(DarkTheme.ACCENT_PURPLE), "Specifications", TitledBorder.LEADING, TitledBorder.TOP, DarkTheme.FONT_BTN, DarkTheme.ACCENT_PURPLE));
        txtFinalSummary = new JTextArea();
        txtFinalSummary.setEditable(false);
        txtFinalSummary.setFont(DarkTheme.FONT_MONO);
        txtFinalSummary.setBackground(DarkTheme.BG_PANEL);
        txtFinalSummary.setForeground(Color.WHITE); 
        txtFinalSummary.setMargin(new Insets(20, 20, 20, 20));
        sumPanel.add(txtFinalSummary, BorderLayout.CENTER);
        rightPanel.add(sumPanel);
        
        JPanel convPanel = new JPanel(new BorderLayout());
        convPanel.setBackground(DarkTheme.BG_PANEL);
        convPanel.setBorder(new TitledBorder(new LineBorder(DarkTheme.TEXT_GREEN), "Real-time Value", TitledBorder.LEADING, TitledBorder.TOP, DarkTheme.FONT_BTN, DarkTheme.TEXT_GREEN));
        lblConversionResult = new JLabel("...");
        lblConversionResult.setFont(new Font("Consolas", Font.BOLD, 16));
        lblConversionResult.setForeground(DarkTheme.TEXT_GREEN); 
        lblConversionResult.setHorizontalAlignment(SwingConstants.CENTER);
        convPanel.add(lblConversionResult, BorderLayout.CENTER);
        rightPanel.add(convPanel);

        splitPane.setRightComponent(rightPanel);
        panel.add(splitPane, BorderLayout.CENTER);
        
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(DarkTheme.BG_MAIN);
        JButton btnExp = DarkTheme.createButton("EXPORT REPORT");
        btnExp.addActionListener(e -> {
            String filename = "Report_" + selectedCurrency.code + ".txt";
            try (FileWriter fw = new FileWriter(filename)) {
                double amount = Double.parseDouble(selectedDenom);
                 double valUSD = amount * selectedCurrency.rateToUSD;
                 double valPHP = valUSD * 58.50; 
                DecimalFormat df = new DecimalFormat("#,##0.00");
                
                fw.write("CURRENCY REPORT: " + selectedCurrency.name + " (" + selectedCurrency.code + ")\n");
                fw.write("Denomination: " + selectedDenom + "\n");
                fw.write("================================================================================\n\n");
                fw.write("[ SECTION 1: HISTORICAL & ECONOMIC ANALYSIS ]\n\n");
                fw.write(txtFacts.getText());
                fw.write("\n\n================================================================================\n\n");
                fw.write("[ SECTION 2: SPECIFICATIONS ]\n\n");
                fw.write(txtFinalSummary.getText());
                fw.write("\n\n================================================================================\n\n");
                fw.write("[ SECTION 3: REAL-TIME CONVERSION ]\n\n");
                fw.write("USD: $" + df.format(valUSD) + "\n");
                 fw.write("EUR: €" + df.format(valUSD * 0.92) + "\n");
                 fw.write("GBP: £" + df.format(valUSD * 0.78) + "\n");
                fw.write("PHP: ₱" + df.format(valPHP) + "\n");
                
                JOptionPane.showMessageDialog(this, "Report saved successfully to " + filename, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE); }
        });
        south.add(btnExp);
        panel.add(south, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadAnalysis() {
        lblResultHeader.setText("Analysis: " + selectedDenom + " " + selectedCurrency.code);
        txtFacts.setText(LoreGenerator.generateHistory(selectedCurrency, selectedDenom));
        txtFacts.setCaretPosition(0);
        txtFinalSummary.setText(LoreGenerator.generateSummary(selectedCurrency, selectedDenom));
        try {
            double amount = Double.parseDouble(selectedDenom);
            double valUSD = amount * selectedCurrency.rateToUSD;
            double valPHP = valUSD * 58.50; 
            DecimalFormat df = new DecimalFormat("#,##0.00");
            lblConversionResult.setText("<html><center>USD: $" + df.format(valUSD) + "<br>EUR: €" + df.format(valUSD*0.92) + "<br>GBP: £" + df.format(valUSD*0.78) + "<br><br><font color='#40B4FF'>PHP: ₱" + df.format(valPHP) + "</font></center></html>");
        } catch (Exception e) { lblConversionResult.setText("N/A"); }
    }

    private void initializeGlobalData() {
        allCurrencies = new ArrayList<>();
        String[] stdDens = {"100", "50", "20", "10", "5", "1", "0.25", "0.10", "0.05"};
        String[] highDens = {"10000", "5000", "2000", "1000", "500", "100", "50", "10"};
        String[] midDens  = {"1000", "500", "200", "100", "50", "20", "10", "5", "1"};
        Color C_GREEN = new Color(133, 187, 101); Color C_BLUE  = new Color(100, 200, 255);
        Color C_RED   = new Color(255, 100, 100); Color C_YELL  = new Color(240, 230, 150);
        Color C_ORNG  = new Color(255, 165, 0);   Color C_PURP  = new Color(200, 160, 255);
        Color C_GRAY  = new Color(200, 200, 200); Color C_BROWN = new Color(139, 69, 19);

        // ASIA (45+)
        addCur("PHP", "Philippine Peso", "Asia", "₱", 0.017, new String[]{"1000","500","200","100","50","20","10","5","1"}, "Bangko Sentral", "Polymer/Paper", "Piso", "Peso", "Sentimo", "1949", C_ORNG, 20.0);
        addCur("JPY", "Japanese Yen", "Asia", "¥", 0.0067, highDens, "Bank of Japan", "Mitsumata", "En", "Yen", "Sen", "1871", C_YELL, 500.0);
        addCur("CNY", "Chinese Yuan", "Asia", "¥", 0.14, stdDens, "PBOC", "Cotton Paper", "RMB", "Yuan", "Jiao", "1948", C_RED, 1.0);
        addCur("INR", "Indian Rupee", "Asia", "₹", 0.012, new String[]{"2000","500","200","100","50","20","10","5"}, "RBI", "Cotton Pulp", "Rupya", "Rupee", "Paisa", "1935", C_PURP, 20.0);
        addCur("KRW", "South Korean Won", "Asia", "₩", 0.00075, highDens, "Bank of Korea", "Cotton", "Won", "Won", "Jeon", "1902", C_BLUE, 500.0);
        addCur("IDR", "Indonesian Rupiah", "Asia", "Rp", 0.000064, new String[]{"100000","50000","20000","10000","5000","2000","1000"}, "Bank Indonesia", "Paper", "Perak", "Rupiah", "Sen", "1946", C_RED, 1000.0);
        addCur("THB", "Thai Baht", "Asia", "฿", 0.028, midDens, "Bank of Thailand", "Paper", "Baht", "Baht", "Satang", "1897", C_BLUE, 10.0);
        addCur("VND", "Vietnamese Dong", "Asia", "₫", 0.000041, new String[]{"500000","200000","100000","50000","20000"}, "State Bank of VN", "Polymer", "Dong", "Dong", "Hao", "1978", C_GREEN, 5000.0);
        addCur("MYR", "Malaysian Ringgit", "Asia", "RM", 0.21, midDens, "Bank Negara", "Polymer", "Ringgit", "Ringgit", "Sen", "1967", C_RED, 1.0);
        addCur("SGD", "Singapore Dollar", "Asia", "S$", 0.74, stdDens, "MAS", "Polymer", "Sing", "Dollar", "Cent", "1967", C_BLUE, 1.0);
        addCur("HKD", "Hong Kong Dollar", "Asia", "HK$", 0.13, stdDens, "HKMA", "Paper", "Honkie", "Dollar", "Cent", "1845", C_BLUE, 10.0);
        addCur("TWD", "New Taiwan Dollar", "Asia", "NT$", 0.032, new String[]{"2000","1000","500","100","50","10"}, "Central Bank", "Paper", "Kuai", "Dollar", "Fen", "1949", C_RED, 50.0);
        addCur("PKR", "Pakistani Rupee", "Asia", "₨", 0.0036, midDens, "State Bank", "Paper", "Rupaya", "Rupee", "Paisa", "1948", C_GREEN, 10.0);
        addCur("BDT", "Bangladeshi Taka", "Asia", "৳", 0.0091, midDens, "Bangladesh Bank", "Paper", "Taka", "Taka", "Poisha", "1972", C_PURP, 5.0);
        addCur("LKR", "Sri Lankan Rupee", "Asia", "Rs", 0.0033, midDens, "Central Bank", "Paper", "Rupiyal", "Rupee", "Cents", "1885", C_ORNG, 20.0);
        addCur("KZT", "Kazakhstani Tenge", "Asia", "₸", 0.0022, midDens, "NBK", "Paper", "Tenge", "Tenge", "Tiyn", "1993", C_BLUE, 100.0);
        addCur("UZS", "Uzbekistani Som", "Asia", "лв", 0.00008, new String[]{"100000","50000","10000","5000","1000"}, "CBU", "Paper", "Som", "Som", "Tiyin", "1993", C_GREEN, 1000.0);
        addCur("MNT", "Mongolian Tugrik", "Asia", "₮", 0.00029, midDens, "Bank of Mongolia", "Paper", "Tugrik", "Tugrik", "Mongo", "1925", C_BROWN, 500.0);
        addCur("NPR", "Nepalese Rupee", "Asia", "Rs", 0.0075, midDens, "Nepal Rastra", "Paper", "Rupee", "Rupee", "Paisa", "1932", C_RED, 5.0);
        addCur("MMK", "Myanmar Kyat", "Asia", "K", 0.00048, midDens, "CBM", "Paper", "Kyat", "Kyat", "Pya", "1852", C_PURP, 50.0);
        addCur("LAK", "Lao Kip", "Asia", "₭", 0.000048, midDens, "Bank of Lao", "Paper", "Kip", "Kip", "Att", "1952", C_BLUE, 1000.0);
        addCur("KHR", "Cambodian Riel", "Asia", "៛", 0.00024, midDens, "NBC", "Paper", "Riel", "Riel", "Kak", "1980", C_ORNG, 100.0);
        addCur("MVR", "Maldivian Rufiyaa", "Asia", "Rf", 0.065, midDens, "MMA", "Polymer", "Rufiyaa", "Rufiyaa", "Laari", "1947", C_BLUE, 2.0);
        addCur("AFN", "Afghan Afghani", "Asia", "؋", 0.014, midDens, "DAB", "Paper", "Afghani", "Afghani", "Pul", "1925", C_GREEN, 5.0);
        addCur("BTN", "Bhutanese Ngultrum", "Asia", "Nu.", 0.012, midDens, "RMA", "Paper", "Ngultrum", "Ngultrum", "Chhertum", "1974", C_YELL, 1.0);
        addCur("KGS", "Kyrgyzstani Som", "Asia", "с", 0.011, midDens, "NBKR", "Paper", "Som", "Som", "Tyiyn", "1993", C_RED, 10.0);
        addCur("TJS", "Tajikistani Somoni", "Asia", "SM", 0.091, midDens, "NBT", "Paper", "Somoni", "Somoni", "Diram", "2000", C_BLUE, 5.0);
        addCur("TMT", "Turkmenistan Manat", "Asia", "m", 0.29, stdDens, "CBT", "Paper", "Manat", "Manat", "Tennesi", "1993", C_GREEN, 1.0);
        addCur("MOP", "Macanese Pataca", "Asia", "MOP$", 0.12, stdDens, "AMCM", "Paper", "Pataca", "Pataca", "Avos", "1894", C_GREEN, 10.0);
        addCur("BND", "Brunei Dollar", "Asia", "B$", 0.74, stdDens, "AMBD", "Polymer", "Dollar", "Dollar", "Sen", "1967", C_YELL, 1.0);
        addCur("KPW", "North Korean Won", "Asia", "₩", 0.0011, highDens, "Central Bank", "Paper", "Won", "Won", "Chon", "1947", C_RED, 500.0);

        // EUROPE (45+)
        addCur("EUR", "Euro", "Europe", "€", 1.09, new String[]{"500","200","100","50","20","10","5","2","1"}, "ECB", "Cotton", "Euro", "Euro", "Cent", "1999", C_YELL, 2.0);
        addCur("GBP", "British Pound", "Europe", "£", 1.27, new String[]{"50","20","10","5","2","1"}, "Bank of England", "Polymer", "Quid", "Pound", "Penny", "700", C_PURP, 2.0);
        addCur("CHF", "Swiss Franc", "Europe", "Fr", 1.13, midDens, "SNB", "Hybrid", "Franc", "Franc", "Rappen", "1850", C_GREEN, 5.0);
        addCur("SEK", "Swedish Krona", "Europe", "kr", 0.096, midDens, "Riksbank", "Paper", "Spann", "Krona", "Ore", "1873", C_GRAY, 10.0);
        addCur("NOK", "Norwegian Krone", "Europe", "kr", 0.094, midDens, "Norges Bank", "Paper", "Kroner", "Krone", "Ore", "1875", C_BLUE, 20.0);
        addCur("DKK", "Danish Krone", "Europe", "kr", 0.15, midDens, "Nationalbank", "Paper", "Kroner", "Krone", "Ore", "1875", C_GRAY, 20.0);
        addCur("PLN", "Polish Zloty", "Europe", "zł", 0.25, midDens, "NBP", "Paper", "Zloty", "Zloty", "Grosz", "1924", C_RED, 5.0);
        addCur("HUF", "Hungarian Forint", "Europe", "Ft", 0.0028, new String[]{"20000","10000","5000","2000","1000","500","200"}, "MNB", "Paper", "Forint", "Forint", "Filler", "1946", C_BROWN, 200.0);
        addCur("CZK", "Czech Koruna", "Europe", "Kč", 0.043, midDens, "CNB", "Paper", "Kačka", "Koruna", "Halér", "1993", C_ORNG, 50.0);
        addCur("RUB", "Russian Ruble", "Europe", "₽", 0.011, midDens, "Bank of Russia", "Paper", "Ruble", "Ruble", "Kopek", "13th C.", C_RED, 10.0);
        addCur("TRY", "Turkish Lira", "Europe", "₺", 0.033, midDens, "CBRT", "Paper", "Lira", "Lira", "Kurus", "1844", C_BLUE, 1.0);
        addCur("UAH", "Ukrainian Hryvnia", "Europe", "₴", 0.026, midDens, "NBU", "Paper", "Hryvnia", "Hryvnia", "Kopiyka", "1996", C_BLUE, 10.0);
        addCur("ISK", "Icelandic Krona", "Europe", "kr", 0.0072, midDens, "Central Bank", "Paper", "Krona", "Krona", "Eyrir", "1918", C_BLUE, 100.0);
        addCur("RON", "Romanian Leu", "Europe", "lei", 0.22, midDens, "BNR", "Polymer", "Leu", "Leu", "Ban", "1867", C_GREEN, 1.0);
        addCur("BGN", "Bulgarian Lev", "Europe", "лв", 0.55, stdDens, "BNB", "Paper", "Lev", "Lev", "Stotinka", "1881", C_PURP, 2.0);
        addCur("RSD", "Serbian Dinar", "Europe", "дин", 0.0093, midDens, "NBS", "Paper", "Dinar", "Dinar", "Para", "2003", C_BLUE, 20.0);
        addCur("ALL", "Albanian Lek", "Europe", "L", 0.011, midDens, "Bank of Albania", "Paper", "Lek", "Lek", "Qindarke", "1926", C_RED, 100.0);
        addCur("BAM", "Bosnia Mark", "Europe", "KM", 0.55, midDens, "CBBH", "Paper", "Marka", "Mark", "Fening", "1998", C_BLUE, 5.0);
        addCur("MKD", "Macedonian Denar", "Europe", "ден", 0.018, midDens, "NBRM", "Paper", "Denar", "Denar", "Deni", "1992", C_ORNG, 50.0);
        addCur("BYN", "Belarusian Ruble", "Europe", "Br", 0.30, stdDens, "NBRB", "Paper", "Ruble", "Ruble", "Kopek", "1992", C_GREEN, 2.0);
        addCur("MDL", "Moldovan Leu", "Europe", "L", 0.056, midDens, "NBM", "Paper", "Leu", "Leu", "Ban", "1993", C_BLUE, 10.0);
        addCur("GEL", "Georgian Lari", "Europe", "₾", 0.37, midDens, "NBG", "Paper", "Lari", "Lari", "Tetri", "1995", C_PURP, 2.0);
        addCur("AMD", "Armenian Dram", "Europe", "֏", 0.0025, new String[]{"20000","10000","5000","1000","500","200","100"}, "CBA", "Paper", "Dram", "Dram", "Luma", "1993", C_ORNG, 500.0);
        addCur("GIP", "Gibraltar Pound", "Europe", "£", 1.27, stdDens, "Govt of Gibraltar", "Polymer", "Pound", "Pound", "Penny", "1927", C_RED, 1.0);
        addCur("JEP", "Jersey Pound", "Europe", "£", 1.27, stdDens, "States of Jersey", "Paper", "Pound", "Pound", "Penny", "1837", C_GREEN, 1.0);
        addCur("GGP", "Guernsey Pound", "Europe", "£", 1.27, stdDens, "States of Guernsey", "Paper", "Pound", "Pound", "Penny", "1921", C_BLUE, 1.0);
        addCur("IMP", "Manx Pound", "Europe", "£", 1.27, stdDens, "Isle of Man Govt", "Paper", "Pound", "Pound", "Penny", "1840", C_GRAY, 1.0);
        addCur("AZN", "Azerbaijani Manat", "Europe", "₼", 0.59, stdDens, "CBA", "Paper", "Manat", "Manat", "Qapik", "1992", C_PURP, 1.0);

        // NORTH AMERICA (20+)
        addCur("USD", "US Dollar", "North America", "$", 1.0, stdDens, "Federal Reserve", "Cotton-Linen", "Greenback", "Dollar", "Cent", "1792", C_GREEN, 0.99);
        addCur("CAD", "Canadian Dollar", "North America", "C$", 0.74, stdDens, "Bank of Canada", "Polymer", "Loonie", "Dollar", "Cent", "1858", C_BLUE, 2.0);
        addCur("MXN", "Mexican Peso", "North America", "$", 0.058, midDens, "Banxico", "Polymer", "Peso", "Peso", "Centavo", "1863", C_PURP, 20.0);
        addCur("GTQ", "Guatemalan Quetzal", "North America", "Q", 0.13, midDens, "Bank of Guatemala", "Paper", "Quetzal", "Quetzal", "Centavo", "1925", C_GREEN, 1.0);
        addCur("HNL", "Honduran Lempira", "North America", "L", 0.041, midDens, "Central Bank", "Paper", "Lempira", "Lempira", "Centavo", "1931", C_RED, 1.0);
        addCur("NIO", "Nicaraguan Cordoba", "North America", "C$", 0.027, midDens, "BCN", "Polymer", "Cordoba", "Cordoba", "Centavo", "1912", C_YELL, 5.0);
        addCur("CRC", "Costa Rican Colon", "North America", "₡", 0.0019, new String[]{"20000","10000","5000","2000","1000","500","100"}, "BCCR", "Polymer", "Peso", "Colon", "Centimo", "1896", C_ORNG, 500.0);
        addCur("PAB", "Panamanian Balboa", "North America", "B/.", 1.0, stdDens, "National Bank", "Paper", "Balboa", "Balboa", "Centesimo", "1904", C_GRAY, 1.0);
        addCur("BZD", "Belize Dollar", "North America", "BZ$", 0.50, stdDens, "CBB", "Paper", "Dollar", "Dollar", "Cent", "1973", C_BLUE, 1.0);
        addCur("BMD", "Bermudian Dollar", "North America", "BD$", 1.0, stdDens, "BMA", "Polymer", "Dollar", "Dollar", "Cent", "1970", C_RED, 1.0);

        // CARIBBEAN (15+)
        addCur("JMD", "Jamaican Dollar", "Caribbean", "J$", 0.0065, midDens, "Bank of Jamaica", "Paper", "Jay", "Dollar", "Cent", "1969", C_YELL, 20.0);
        addCur("HTG", "Haitian Gourde", "Caribbean", "G", 0.0076, midDens, "BRH", "Paper", "Gourde", "Gourde", "Centime", "1813", C_GRAY, 5.0);
        addCur("DOP", "Dominican Peso", "Caribbean", "RD$", 0.017, midDens, "Central Bank", "Paper", "Peso", "Peso", "Centavo", "1844", C_ORNG, 25.0);
        addCur("BSD", "Bahamian Dollar", "Caribbean", "B$", 1.0, stdDens, "Central Bank", "Paper", "Dollar", "Dollar", "Cent", "1966", C_BLUE, 0.25);
        addCur("TTD", "Trinidad Dollar", "Caribbean", "TT$", 0.15, stdDens, "Central Bank", "Polymer", "Dollar", "Dollar", "Cent", "1964", C_RED, 1.0);
        addCur("BBD", "Barbadian Dollar", "Caribbean", "Bds$", 0.50, stdDens, "CBB", "Paper", "Dollar", "Dollar", "Cent", "1973", C_BLUE, 1.0);
        addCur("XCD", "East Caribbean Dollar", "Caribbean", "$", 0.37, stdDens, "ECCB", "Polymer", "EC", "Dollar", "Cent", "1965", C_GREEN, 1.0);
        addCur("KYD", "Cayman Is. Dollar", "Caribbean", "CI$", 1.20, stdDens, "CIMA", "Paper", "Dollar", "Dollar", "Cent", "1972", C_BLUE, 0.25);
        addCur("CUP", "Cuban Peso", "Caribbean", "₱", 0.042, midDens, "Central Bank", "Paper", "Peso", "Peso", "Centavo", "1857", C_PURP, 1.0);
        addCur("AWG", "Aruban Florin", "Caribbean", "Afl", 0.56, stdDens, "CBA", "Paper", "Florin", "Florin", "Cent", "1986", C_ORNG, 5.0);
        addCur("ANG", "N. Antillean Guilder", "Caribbean", "NAf", 0.56, stdDens, "CBCS", "Paper", "Guilder", "Guilder", "Cent", "1952", C_YELL, 1.0);

        // SOUTH AMERICA (13+)
        addCur("BRL", "Brazilian Real", "South America", "R$", 0.20, stdDens, "BCB", "Paper", "Real", "Real", "Centavo", "1994", C_GREEN, 1.0);
        addCur("ARS", "Argentine Peso", "South America", "$", 0.0012, midDens, "BCRA", "Paper", "Mango", "Peso", "Centavo", "1992", C_BLUE, 100.0);
        addCur("CLP", "Chilean Peso", "South America", "$", 0.0011, new String[]{"20000","10000","5000","2000","1000","500","100"}, "Central Bank", "Polymer", "Luca", "Peso", "Centavo", "1975", C_ORNG, 500.0);
        addCur("COP", "Colombian Peso", "South America", "$", 0.00025, new String[]{"100000","50000","20000","10000","5000","2000","1000"}, "Banrep", "Paper", "Peso", "Peso", "Centavo", "1810", C_YELL, 1000.0);
        addCur("PEN", "Peruvian Sol", "South America", "S/", 0.27, midDens, "BCRP", "Paper", "Sol", "Sol", "Centimo", "1991", C_PURP, 5.0);
        addCur("UYU", "Uruguayan Peso", "South America", "$U", 0.025, midDens, "BCU", "Polymer", "Peso", "Peso", "Centesimo", "1896", C_BLUE, 10.0);
        addCur("PYG", "Paraguayan Guarani", "South America", "₲", 0.00013, new String[]{"100000","50000","20000","10000","5000","2000"}, "BCP", "Polymer", "Guarani", "Guarani", "Centimo", "1944", C_RED, 1000.0);
        addCur("BOB", "Bolivian Boliviano", "South America", "Bs", 0.14, midDens, "BCB", "Paper", "Boliviano", "Boliviano", "Centavo", "1864", C_GREEN, 5.0);
        addCur("VES", "Venezuelan Bolivar", "South America", "Bs", 0.028, stdDens, "BCV", "Paper", "Bolivar", "Bolivar", "Centimo", "2018", C_RED, 1.0);
        addCur("GYD", "Guyanese Dollar", "South America", "$", 0.0048, midDens, "Bank of Guyana", "Paper", "Dollar", "Dollar", "Cent", "1839", C_ORNG, 20.0);
        addCur("SRD", "Surinamese Dollar", "South America", "$", 0.026, midDens, "CBvS", "Paper", "Dollar", "Dollar", "Cent", "2004", C_BLUE, 2.0);
        addCur("FKP", "Falkland Is. Pound", "South America", "£", 1.27, stdDens, "Commissioners", "Paper", "Pound", "Pound", "Penny", "1899", C_GRAY, 1.0);

        // AFRICA (50+)
        addCur("ZAR", "South African Rand", "Africa", "R", 0.053, new String[]{"200","100","50","20","10","5","2","1"}, "SARB", "Paper", "Rand", "Rand", "Cent", "1961", C_ORNG, 5.0);
        addCur("EGP", "Egyptian Pound", "Africa", "£", 0.032, midDens, "CBE", "Paper", "Geneih", "Pound", "Piastre", "1834", C_YELL, 1.0);
        addCur("NGN", "Nigerian Naira", "Africa", "₦", 0.0011, midDens, "CBN", "Paper", "Naira", "Naira", "Kobo", "1973", C_GREEN, 50.0);
        addCur("KES", "Kenyan Shilling", "Africa", "KSh", 0.0063, midDens, "CBK", "Paper", "Bob", "Shilling", "Cent", "1966", C_RED, 40.0);
        addCur("GHS", "Ghanaian Cedi", "Africa", "₵", 0.083, midDens, "BoG", "Paper", "Cedi", "Cedi", "Pesewa", "1965", C_BLUE, 2.0);
        addCur("MAD", "Moroccan Dirham", "Africa", "DH", 0.099, midDens, "Bank Al-Maghrib", "Paper", "Dirham", "Dirham", "Santim", "1960", C_PURP, 10.0);
        addCur("DZD", "Algerian Dinar", "Africa", "DA", 0.0074, midDens, "Bank of Algeria", "Paper", "Dinar", "Dinar", "Santeem", "1964", C_GREEN, 100.0);
        addCur("TND", "Tunisian Dinar", "Africa", "DT", 0.32, midDens, "Central Bank", "Paper", "Dinar", "Dinar", "Millime", "1960", C_YELL, 5.0);
        addCur("MUR", "Mauritian Rupee", "Africa", "₨", 0.022, midDens, "Bank of Mauritius", "Polymer", "Rupee", "Rupee", "Cent", "1877", C_BLUE, 20.0);
        addCur("ETB", "Ethiopian Birr", "Africa", "Br", 0.018, stdDens, "NBE", "Paper", "Birr", "Birr", "Santim", "1945", C_GREEN, 1.0);
        addCur("TZS", "Tanzanian Shilling", "Africa", "TSh", 0.0004, highDens, "BoT", "Paper", "Shilling", "Shilling", "Senti", "1966", C_BLUE, 500.0);
        addCur("UGX", "Ugandan Shilling", "Africa", "USh", 0.00026, highDens, "BoU", "Paper", "Shilling", "Shilling", "Cent", "1966", C_RED, 1000.0);
        addCur("RWF", "Rwandan Franc", "Africa", "FRw", 0.0008, highDens, "NBR", "Paper", "Franc", "Franc", "Centime", "1964", C_ORNG, 100.0);
        addCur("XOF", "West African CFA", "Africa", "CFA", 0.0016, highDens, "BCEAO", "Paper", "Franc", "Franc", "Centime", "1945", C_GRAY, 500.0);
        addCur("XAF", "Central African CFA", "Africa", "FCFA", 0.0016, highDens, "BEAC", "Paper", "Franc", "Franc", "Centime", "1945", C_BLUE, 500.0);
        addCur("AOA", "Angolan Kwanza", "Africa", "Kz", 0.0012, midDens, "BNA", "Paper", "Kwanza", "Kwanza", "Centimo", "1977", C_RED, 10.0);
        addCur("ZMW", "Zambian Kwacha", "Africa", "ZK", 0.038, stdDens, "BoZ", "Paper", "Kwacha", "Kwacha", "Ngwee", "1968", C_GREEN, 1.0);
        addCur("LYD", "Libyan Dinar", "Africa", "LD", 0.21, midDens, "CBL", "Paper", "Dinar", "Dinar", "Dirham", "1971", C_YELL, 1.0);
        addCur("SCR", "Seychellois Rupee", "Africa", "SR", 0.076, midDens, "CBS", "Paper", "Rupee", "Rupee", "Cent", "1914", C_BLUE, 5.0);
        addCur("BWP", "Botswana Pula", "Africa", "P", 0.074, midDens, "Bank of Botswana", "Paper", "Pula", "Pula", "Thebe", "1976", C_BLUE, 5.0);
        addCur("NAD", "Namibian Dollar", "Africa", "N$", 0.053, midDens, "BoN", "Paper", "Dollar", "Dollar", "Cent", "1993", C_PURP, 5.0);
        addCur("CDF", "Congolese Franc", "Africa", "FC", 0.00036, highDens, "BCC", "Paper", "Franc", "Franc", "Centime", "1997", C_RED, 50.0);
        addCur("DJF", "Djiboutian Franc", "Africa", "Fdj", 0.0056, midDens, "BCD", "Paper", "Franc", "Franc", "Centime", "1949", C_GREEN, 50.0);
        addCur("ERN", "Eritrean Nakfa", "Africa", "Nfk", 0.066, midDens, "Bank of Eritrea", "Paper", "Nakfa", "Nakfa", "Cent", "1997", C_ORNG, 1.0);
        addCur("SZL", "Swazi Lilangeni", "Africa", "L", 0.053, midDens, "CBE", "Paper", "Lilangeni", "Lilangeni", "Cent", "1974", C_BLUE, 5.0);
        addCur("LSL", "Lesotho Loti", "Africa", "L", 0.053, midDens, "CBL", "Paper", "Loti", "Loti", "Sente", "1966", C_GREEN, 5.0);
        addCur("GMD", "Gambian Dalasi", "Africa", "D", 0.015, midDens, "CBG", "Paper", "Dalasi", "Dalasi", "Butut", "1971", C_RED, 1.0);
        addCur("GNF", "Guinean Franc", "Africa", "FG", 0.00011, highDens, "BCRG", "Paper", "Franc", "Franc", "Centime", "1959", C_YELL, 500.0);
        addCur("LRD", "Liberian Dollar", "Africa", "L$", 0.0052, midDens, "CBL", "Paper", "Dollar", "Dollar", "Cent", "1847", C_GRAY, 5.0);
        addCur("MWK", "Malawian Kwacha", "Africa", "MK", 0.00058, highDens, "RBM", "Paper", "Kwacha", "Kwacha", "Tambala", "1964", C_ORNG, 20.0);
        addCur("MGA", "Malagasy Ariary", "Africa", "Ar", 0.00022, highDens, "BFM", "Paper", "Ariary", "Ariary", "Iraimbilanja", "1961", C_PURP, 100.0);
        addCur("MRU", "Mauritanian Ouguiya", "Africa", "UM", 0.025, stdDens, "BCM", "Polymer", "Ouguiya", "Ouguiya", "Khoums", "1973", C_GREEN, 1.0);
        addCur("MZN", "Mozambican Metical", "Africa", "MT", 0.016, midDens, "Bank of Moz", "Paper", "Metical", "Metical", "Centavo", "1980", C_BLUE, 10.0);
        addCur("STN", "Sao Tome Dobra", "Africa", "Db", 0.044, stdDens, "BCSTP", "Paper", "Dobra", "Dobra", "Centimo", "1977", C_RED, 10.0);
        addCur("SLL", "Sierra Leonean Leone", "Africa", "Le", 0.000044, highDens, "BSL", "Paper", "Leone", "Leone", "Cent", "1964", C_GREEN, 500.0);
        addCur("SOS", "Somali Shilling", "Africa", "Sh.So.", 0.0017, midDens, "CBS", "Paper", "Shilling", "Shilling", "Senti", "1962", C_YELL, 50.0);
        addCur("SSP", "South Sudanese Pound", "Africa", "£", 0.0076, midDens, "BoSS", "Paper", "Pound", "Pound", "Piastre", "2011", C_ORNG, 5.0);
        addCur("SDG", "Sudanese Pound", "Africa", "£", 0.0017, midDens, "CBOS", "Paper", "Pound", "Pound", "Piastre", "1956", C_BLUE, 5.0);
        addCur("SHP", "Saint Helena Pound", "Africa", "£", 1.27, stdDens, "Govt of St Helena", "Paper", "Pound", "Pound", "Penny", "1976", C_PURP, 1.0);
        addCur("CVE", "Cape Verdean Escudo", "Africa", "Esc", 0.0097, midDens, "BCV", "Paper", "Escudo", "Escudo", "Centavo", "1914", C_RED, 100.0);
        addCur("KMF", "Comorian Franc", "Africa", "CF", 0.0022, highDens, "Central Bank", "Paper", "Franc", "Franc", "Centime", "1981", C_GREEN, 50.0);
        addCur("BIF", "Burundian Franc", "Africa", "FBu", 0.00035, highDens, "BRB", "Paper", "Franc", "Franc", "Centime", "1964", C_GRAY, 100.0);

        // MIDDLE EAST (15+)
        addCur("SAR", "Saudi Riyal", "Middle East", "﷼", 0.27, midDens, "SAMA", "Paper", "Riyal", "Riyal", "Halala", "1925", C_GREEN, 2.0);
        addCur("AED", "UAE Dirham", "Middle East", "د.إ", 0.27, midDens, "Central Bank", "Paper", "Dirham", "Dirham", "Fils", "1973", C_PURP, 1.0);
        addCur("ILS", "Israeli Shekel", "Middle East", "₪", 0.27, midDens, "Bank of Israel", "Polymer", "Shekel", "Shekel", "Agora", "1986", C_BLUE, 10.0);
        addCur("QAR", "Qatari Riyal", "Middle East", "﷼", 0.27, midDens, "QCB", "Paper", "Riyal", "Riyal", "Dirham", "1973", C_PURP, 1.0);
        addCur("KWD", "Kuwaiti Dinar", "Middle East", "KD", 3.25, new String[]{"20","10","5","1","0.5","0.25"}, "CBK", "Paper", "Dinar", "Dinar", "Fils", "1960", C_BLUE, 0.25);
        addCur("OMR", "Omani Rial", "Middle East", "﷼", 2.60, new String[]{"50","20","10","5","1","0.5"}, "CBO", "Paper", "Rial", "Rial", "Baisa", "1970", C_RED, 0.5);
        addCur("BHD", "Bahraini Dinar", "Middle East", "BD", 2.65, new String[]{"20","10","5","1","0.5"}, "CBB", "Paper", "Dinar", "Dinar", "Fils", "1965", C_ORNG, 0.5);
        addCur("JOD", "Jordanian Dinar", "Middle East", "JD", 1.41, new String[]{"50","20","10","5","1"}, "CBJ", "Paper", "Dinar", "Dinar", "Piastre", "1950", C_GREEN, 1.0);
        addCur("LBP", "Lebanese Pound", "Middle East", "L£", 0.000011, midDens, "BDL", "Paper", "Lira", "Pound", "Piastre", "1924", C_YELL, 1000.0);
        addCur("IRR", "Iranian Rial", "Middle East", "﷼", 0.000024, highDens, "CBI", "Paper", "Rial", "Rial", "Dinar", "1932", C_RED, 5000.0);
        addCur("IQD", "Iraqi Dinar", "Middle East", "ع.د", 0.00076, new String[]{"50000","25000","10000","5000","1000","500","250"}, "CBI", "Paper", "Dinar", "Dinar", "Fils", "1932", C_ORNG, 500.0);
        addCur("YER", "Yemeni Rial", "Middle East", "﷼", 0.0040, midDens, "CBY", "Paper", "Rial", "Rial", "Fils", "1990", C_BLUE, 20.0);
        addCur("SYP", "Syrian Pound", "Middle East", "LS", 0.000077, midDens, "CBS", "Paper", "Pound", "Pound", "Piastre", "1919", C_GRAY, 50.0);

        // OCEANIA (12+)
        addCur("AUD", "Australian Dollar", "Oceania", "A$", 0.65, stdDens, "RBA", "Polymer", "Aussie", "Dollar", "Cent", "1966", C_ORNG, 2.0);
        addCur("NZD", "New Zealand Dollar", "Oceania", "NZ$", 0.61, stdDens, "RBNZ", "Polymer", "Kiwi", "Dollar", "Cent", "1967", C_GRAY, 2.0);
        addCur("PGK", "PNG Kina", "Oceania", "K", 0.26, midDens, "Bank of PNG", "Polymer", "Kina", "Kina", "Toea", "1975", C_GREEN, 1.0);
        addCur("FJD", "Fijian Dollar", "Oceania", "FJ$", 0.44, stdDens, "RBF", "Polymer", "Dollar", "Dollar", "Cent", "1969", C_BLUE, 2.0);
        addCur("VUV", "Vanuatu Vatu", "Oceania", "Vt", 0.0084, highDens, "RBV", "Paper", "Vatu", "Vatu", "None", "1982", C_GREEN, 100.0);
        addCur("WST", "Samoan Tala", "Oceania", "T", 0.36, stdDens, "CBS", "Polymer", "Tala", "Tala", "Sene", "1967", C_RED, 2.0);
        addCur("TOP", "Tongan Pa'anga", "Oceania", "T$", 0.42, stdDens, "NRBT", "Paper", "Pa'anga", "Pa'anga", "Seniti", "1967", C_ORNG, 1.0);
        addCur("SBD", "Solomon Is. Dollar", "Oceania", "SI$", 0.12, stdDens, "CBSI", "Paper", "Dollar", "Dollar", "Cent", "1977", C_BLUE, 2.0);
    }
    
    private void addCur(String c, String n, String r, String s, double rt, String[] d, String b, String m, String ni, String unit, String f, String y, Color cl, double th) {
        allCurrencies.add(new CurrencyData(c, n, r, s, rt, d, b, m, ni, unit, f, y, cl, th));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new GlobalCurrencyApp().setVisible(true));
    }
}