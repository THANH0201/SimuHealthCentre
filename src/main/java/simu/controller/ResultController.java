package simu.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultController {
    // FXML UI components
//    @FXML
//    private TableView<ServiceData> tableView;
//    @FXML
//    private TableColumn<ServiceData, String> serviceColumn;
//    @FXML
//    private TableColumn<ServiceData, Integer> servicePointNumbersColumn;
//    @FXML
//    private TableColumn<ServiceData, Integer> serviceTimeColumn;
    @FXML
    private PieChart totalCustomers;
    @FXML
    private BarChart<String, Number> utilizationChart;
    @FXML
    private Label totalCustomerCount;
    @FXML
    private Label avgWaitingTime;
    @FXML
    private Button backButton;

    private Parent root;
    private int registers, nurses, generals, specialists, laboratories;
    private int avgRegister, avgNurse, avgGeneral, avgSpecialist, avgLaboratory;
    private HashMap<String, Double> piechartData = new HashMap<>();
    private ArrayList<Double> registerUtilization = new ArrayList<>();
    private ArrayList<Double> nursesUtilization = new ArrayList<>();
    private ArrayList<Double> generalUtilization = new ArrayList<>();
    private ArrayList<Double> specialistUtilization = new ArrayList<>();
    private ArrayList<Double> laboratoryUtilization = new ArrayList<>();

    @FXML
    public void initialize() {

    /// /        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
    /// /        servicePointNumbersColumn.setCellValueFactory(new PropertyValueFactory<>("servicePointNumbers"));
    /// /        serviceTimeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceTime"));

        utilizationChart.setTitle("Utilization Efficiency (%)");
        utilizationChart.getXAxis().setLabel("Service Units");
        utilizationChart.getYAxis().setLabel("Utilization");
    }

    @FXML
    public void backButtonAction(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationConfig.fxml"));
            Parent mainMenuRoot = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene mainMenuScene = new Scene(mainMenuRoot);
            stage.setScene(mainMenuScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRoot(Parent resultRoot) {
        this.root = resultRoot;
    }

    public static class ServiceData {
        private final String service;
        private final int servicePointNumbers;
        private final int serviceTime;

        public ServiceData(String service, int servicePointNumbers, int serviceTime) {
            this.service = service;
            this.servicePointNumbers = servicePointNumbers;
            this.serviceTime = serviceTime;
        }

        public String getService() {
            return this.service;
        }

        public int getServicePointNumbers() {
            return this.servicePointNumbers;
        }

        public int getServiceTime() {
            return this.serviceTime;
        }
    }

    public void setResults(double avgWaitTime, List<Integer> customerCount, List<Double> utilization) {
        avgWaitingTime.setText(String.format("%.2f", avgWaitTime));
        totalCustomerCount.setText(String.valueOf(customerCount.stream().mapToInt(Integer::intValue).sum()));

        processStations("Register", registers, customerCount, utilization, registerUtilization);
        processStations("Nurses Desk", nurses, customerCount, utilization, nursesUtilization);
        processStations("General Doctor", generals, customerCount, utilization, generalUtilization);
        processStations("Specialist", specialists, customerCount, utilization, specialistUtilization);
        processStations("Laboratory", laboratories, customerCount, utilization, laboratoryUtilization);

        if (totalCustomers != null) {
            piechartData.forEach((key, value) -> totalCustomers.getData().add(new PieChart.Data(key, value)));
        }
        if (utilizationChart != null) {
            XYChart.Series<String, Number> registerDeskUtilization = createChartSeries("Register", registerUtilization);
            XYChart.Series<String, Number> nurseDeskUtilization = createChartSeries("Nurses Desk", nursesUtilization);
            XYChart.Series<String, Number> generalExamUtilization = createChartSeries("General Doctor", generalUtilization);
            XYChart.Series<String, Number> specialistExamUtilization = createChartSeries("Specialist", specialistUtilization);
            XYChart.Series<String, Number> laboratoryExamUtilization = createChartSeries("Laboratory", laboratoryUtilization);
            utilizationChart.getData().addAll(registerDeskUtilization, nurseDeskUtilization, generalExamUtilization, specialistExamUtilization, laboratoryExamUtilization);
        }
    }

    private void processStations(String stationName, int stationCount, List<Integer> customerCount, List<Double> utilization, List<Double> utilizationList) {
        for (int i = 1; i <= stationCount; i++) {
            piechartData.put(stationName + " " + i, (double) customerCount.remove(0));
            utilizationList.add(utilization.remove(0));
        }
    }

    public void setTable(int registers, int nurses, int generals, int specialists, int laboratories,
                         int avgRegister, int avgNurse, int avgGeneral, int avgSpecialist, int avgLaboratory) {
        this.registers = registers;
        this.nurses = nurses;
        this.generals = generals;
        this.specialists = specialists;
        this.laboratories = laboratories;

        this.avgRegister = avgRegister;
        this.avgNurse = avgNurse;
        this.avgGeneral = avgGeneral;
        this.avgSpecialist = avgSpecialist;
        this.avgLaboratory = avgLaboratory;

//        if (tableView != null) {
//            tableView.getItems().addAll(
//                    new ServiceData("Register", registers, avgRegister),
//                    new ServiceData("Nurses Desk", nurses, avgNurse),
//                    new ServiceData("General Doctor", generals, avgGeneral),
//                    new ServiceData("Specialist", specialists, avgSpecialist),
//                    new ServiceData("Laboratory", laboratories, avgLaboratory));
//        }
    }

    public void display(double avgWaitTime, List<Integer> customerCount, List<Double> utilization, Stage stage) {
        setResults(avgWaitTime, customerCount, utilization);
        stage.setScene(new Scene(root));
    }

        private XYChart.Series<String, Number> createChartSeries(String name, List<Double> utilizationList) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < utilizationList.size(); i++) {
            String servicePoint = String.valueOf(i + 1);
            series.getData().add(new XYChart.Data<>("Service Point " + servicePoint, utilizationList.get(i)));
        }
        return series;
    }
//    @FXML
//    private Label totalCustomersLabel;
//    @FXML
//    private Label endTimeLabel;
//    @FXML
//    private Label avgWaitingLabel;
//
////    @FXML
////    private ListView<ServicePoint> listView;
//
//    @FXML
//    private BarChart<String, Number> utilizationChart;
//    @FXML
//    private CategoryAxis utilXAxis;
//    @FXML
//    private NumberAxis utilYAxis;
//
//    private final ObservableList<ServicePoint> data = FXCollections.observableArrayList();
//    private final XYChart.Series<String, Number> utilSeries = new XYChart.Series<>();
//
//    @FXML
//    private PieChart customersPie;
//
//    public static class ServicePoint {
//        private final IntegerProperty id = new SimpleIntegerProperty();
//        private final DoubleProperty totalServiceTime = new SimpleDoubleProperty();
//        private final DoubleProperty meanServiceTime = new SimpleDoubleProperty();
//        private final IntegerProperty served = new SimpleIntegerProperty();
//        private final DoubleProperty utilization = new SimpleDoubleProperty();
//
//        public ServicePoint(int id, double totalServiceTime, double meanServiceTime, int served, double utilization) {
//            this.id.set(id);
//            this.totalServiceTime.set(totalServiceTime);
//            this.meanServiceTime.set(meanServiceTime);
//            this.served.set(served);
//            this.utilization.set(utilization);
//        }
//
//        public int getId() {
//            return id.get();
//        }
//
//        public IntegerProperty idProperty() {
//            return id;
//        }
//
//        public double getTotalServiceTime() {
//            return totalServiceTime.get();
//        }
//
//        public DoubleProperty totalServiceTimeProperty() {
//            return totalServiceTime;
//        }
//
//        public double getMeanServiceTime() {
//            return meanServiceTime.get();
//        }
//
//        public DoubleProperty meanServiceTimeProperty() {
//            return meanServiceTime;
//        }
//
//        public int getServed() {
//            return served.get();
//        }
//
//        public IntegerProperty servedProperty() {
//            return served;
//        }
//
//        public double getUtilization() {
//            return utilization.get();
//        }
//
//        public DoubleProperty utilizationProperty() {
//            return utilization;
//        }
//
//        public String getName() {
//            return "SP " + getId();
//        }
//    }
//
//    @FXML
//    public void initialize() {
//        // configure list simu.view with a custom cell that formats the service point metrics in a row-like HBox
////        listView.setCellFactory(lv -> new ListCell<>() {
////            @Override
////            protected void updateItem(ServicePoint sp, boolean empty) {
////                super.updateItem(sp, empty);
////                if (empty || sp == null) {
////                    setText(null);
////                    setGraphic(null);
////                } else {
////                    HBox row = new HBox(12);
////                    Text id = new Text("SP " + sp.getId());
////                    Text total = new Text(String.format("Total: %.1f", sp.getTotalServiceTime()));
////                    Text mean = new Text(String.format("Mean: %.1f", sp.getMeanServiceTime()));
////                    Text served = new Text(String.format("Cust: %d", sp.getServed()));
////                    Text util = new Text(String.format("Util: %.2f", sp.getUtilization()));
////                    HBox.setHgrow(id, Priority.NEVER);
////                    HBox.setHgrow(total, Priority.ALWAYS);
////                    row.getChildren().addAll(id, total, mean, served, util);
////                    setGraphic(row);
////                }
////            }
////        });
//
//        // Configure chart
//        utilSeries.setName("Utilization");
//        utilizationChart.getData().add(utilSeries);
//        utilizationChart.setLegendVisible(false);
//
//        // selection: when a user clicks a ListView item, highlight the corresponding bar and slice
////        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> highlightSelected(newSel));
//
//        // sample data (so file is ready-to-run). Replace by calling setReportData(...) from your app.
//        loadSampleData();
//    }
//
//    private void highlightSelected(ServicePoint sp) {
//        // highlight bar
//        for (XYChart.Data<String, Number> d : utilSeries.getData()) {
//            if (d.getNode() == null) continue;
//            if (sp != null && d.getXValue().equals(sp.getName())) {
//                d.getNode().setStyle("-fx-bar-fill: -fx-accent; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 2);");
//            } else {
//                d.getNode().setStyle("");
//            }
//        }
//
//        // highlight pie slice by exploding matching slice (simple approach)
//        for (PieChart.Data slice : customersPie.getData()) {
//            if (sp != null && slice.getName().equals(sp.getName())) {
//                slice.getNode().setOpacity(1.0);
//                slice.getNode().setScaleX(1.05);
//                slice.getNode().setScaleY(1.05);
//            } else {
//                slice.getNode().setOpacity(1.0);
//                slice.getNode().setScaleX(1.0);
//                slice.getNode().setScaleY(1.0);
//            }
//        }
//    }
//
//    public void setReportData(int totalCustomers, double simEnd, double avgWaiting, List<ServicePoint> sps) {
//        Platform.runLater(() -> {
//            totalCustomersLabel.setText("Total customers: " + totalCustomers);
//            endTimeLabel.setText(String.format("Simulation end: %.5f", simEnd));
//            avgWaitingLabel.setText(String.format("Avg waiting: %.5f", avgWaiting));
//
//
//            data.setAll(sps);
////            listView.setItems(data);
//            refreshCharts();
//        });
//    }
//
//    private void refreshCharts() {
//        utilSeries.getData().clear();
//        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
//        for (ServicePoint sp : data) {
//            utilSeries.getData().add(new XYChart.Data<>(sp.getName(), sp.getUtilization()));
//            pieData.add(new PieChart.Data(sp.getName(), sp.getServed()));
//        }
//        customersPie.setData(pieData);
//
//
//// after nodes are created, add tooltips
//        Platform.runLater(() -> {
//            for (XYChart.Data<String, Number> d : utilSeries.getData()) {
//                if (d.getNode() != null) {
//                    String tip = String.format("%sUtil: %.2f Served: %d Mean: %.2f",
//                            d.getXValue(), d.getYValue().doubleValue(),
//                            findByName(d.getXValue()).map(ServicePoint::getServed).orElse(0),
//                            findByName(d.getXValue()).map(ServicePoint::getMeanServiceTime).orElse(0.0));
//                    javafx.scene.control.Tooltip.install(d.getNode(), new javafx.scene.control.Tooltip(tip));
//                }
//            }
//            for (PieChart.Data slice : customersPie.getData()) {
//                String tip = String.format("%sCustomers: %.0f", slice.getName(), slice.getPieValue());
//                if (slice.getNode() != null)
//                    javafx.scene.control.Tooltip.install(slice.getNode(), new javafx.scene.control.Tooltip(tip));
//            }
//        });
//    }
//
//
//    private Optional<ServicePoint> findByName(String name) {
//        return data.stream().filter(sp -> sp.getName().equals(name)).findFirst();
//    }
//
//    // Small helper to populate the UI so the FXML+simu.controller run out-of-the-box
//    // small helper to populate with the sample data
//    private void loadSampleData() {
//        List<ServicePoint> sample = Arrays.asList(
//                new ServicePoint(1, 41.6, 1.8, 23, 0.83),
//                new ServicePoint(2, 55.0, 2.4, 23, 1.10),
//                new ServicePoint(3, 57.4, 5.2, 11, 1.15),
//                new ServicePoint(4, 44.5, 4.5, 10, 0.89),
//                new ServicePoint(5, 47.3, 9.5, 5, 0.95),
//                new ServicePoint(6, 31.5, 10.5, 3, 0.63),
//                new ServicePoint(7, 55.5, 18.5, 3, 1.11),
//                new ServicePoint(8, 40.3, 20.2, 2, 0.81),
//                new ServicePoint(9, 38.4, 19.2, 2, 0.77)
//        );
//        setReportData(52, 50.13296200462718, 0.2524897260426047, sample);
//    }
}
