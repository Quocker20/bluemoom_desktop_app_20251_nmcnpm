package com.bluemoon.app.controller.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.payment.FeeDAO;
import com.bluemoon.app.dao.payment.InvoiceDAO;
import com.bluemoon.app.dao.payment.PaymentDAO;
import com.bluemoon.app.dao.resident.HouseholdDAO;
import com.bluemoon.app.model.Fee;
import com.bluemoon.app.model.Household;
import com.bluemoon.app.model.Invoice;
import com.bluemoon.app.model.Payment;
import com.bluemoon.app.util.AppConstants;
import com.bluemoon.app.util.DatabaseConnector; 

/**
 * Controller for Billing Management.
 * Handles Invoices, Fees, Payments, and Batch Processing.
 */
public class BillingController {

    private final InvoiceDAO invoiceDAO;
    private final FeeDAO feeDAO;
    private final PaymentDAO paymentDAO;
    private final HouseholdDAO householdDAO;
    private final Logger logger;

    public BillingController() {
        this.invoiceDAO = new InvoiceDAO();
        this.feeDAO = new FeeDAO();
        this.paymentDAO = new PaymentDAO();
        this.householdDAO = new HouseholdDAO();
        this.logger = Logger.getLogger(BillingController.class.getName());
    }

    // ==========================================
    //              INVOICE MANAGEMENT
    // ==========================================

    /**
     * Retrieves a list of invoices based on month, year, and search keyword.
     * * @param month   The month filter.
     * @param year    The year filter.
     * @param keyword The search keyword (room number).
     * @return List of Invoice objects.
     */
    public List<Invoice> getInvoices(int month, int year, String keyword) {
        try {
            return invoiceDAO.getAll(month, year, keyword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in getInvoices", e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves invoice details by ID.
     */
    public Invoice getInvoiceById(int id) {
        if (id <= 0) return null;
        try {
            return invoiceDAO.getById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in getInvoiceById", e);
            return null;
        }
    }

    /**
     * Adds a single invoice manually for a specific household.
     */
    public String addSingleInvoice(String roomNumber, Fee fee, int month, int year) {
        try {
            Household household = householdDAO.getByRoomNumber(roomNumber);
            if (household == null) {
                return "Household not found for room: " + roomNumber;
            }

            if (invoiceDAO.exists(household.getId(), fee.getId(), month, year)) {
                return "Invoice for '" + fee.getName() + "' already exists for " + month + "/" + year;
            }

            double amount = 0;
            // Assuming "m2" is the unit for area-based fees. Ensure DB data matches.
            if ("m2".equalsIgnoreCase(fee.getUnit())) {
                amount = fee.getUnitPrice() * household.getArea();
            } else {
                amount = fee.getUnitPrice();
            }

            Invoice invoice = new Invoice();
            invoice.setHouseholdId(household.getId());
            invoice.setFeeTypeId(fee.getId());
            invoice.setMonth(month);
            invoice.setYear(year);
            invoice.setAmountDue(amount);

            invoiceDAO.insert(invoice);
            return "SUCCESS";
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in addSingleInvoice", e);
            return "System Error: " + e.getMessage();
        }
    }

    /**
     * Deletes an invoice by ID.
     */
    public int deleteInvoice(int id) {
        if (id <= 0) return 0;
        try {
            return invoiceDAO.deleteById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in deleteInvoice", e);
            return 0;
        }
    }

    // ==========================================
    //              FEE CONFIGURATION
    // ==========================================

    public List<Fee> getAllFees() {
        try {
            return feeDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in getAllFees", e);
            return Collections.emptyList();
        }
    }

    public List<Fee> searchFees(String keyword) {
        try {
            return feeDAO.getAllActiveFees(keyword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in searchFees", e);
            return Collections.emptyList();
        }
    }

    public boolean addFee(Fee fee) {
        if (fee.getName() == null || fee.getName().trim().isEmpty()) {
            logger.warning("[BillingController] Fee name cannot be empty");
            return false;
        }
        if (fee.getUnitPrice() < 0) {
            logger.warning("[BillingController] Unit price cannot be negative");
            return false;
        }
        // Assuming AppConstants.PHI_BAT_BUOC is renamed to AppConstants.FEE_MANDATORY (value 0)
        if (fee.getType() == AppConstants.PHI_BAT_BUOC && fee.getUnitPrice() <= 0) {
            logger.warning("[BillingController] Mandatory fee must have price > 0");
            return false;
        }
        try {
            return feeDAO.insert(fee);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in addFee", e);
            return false;
        }
    }

    public boolean updateFee(Fee fee) {
        if (fee.getUnitPrice() < 0) return false;
        try {
            return feeDAO.update(fee);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in updateFee", e);
            return false;
        }
    }

    public boolean deleteFee(int id) {
        try {
            return feeDAO.delete(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in deleteFee", e);
            return false;
        }
    }

    public boolean isFeeTypeInUse(int feeId) {
        try {
            return invoiceDAO.isFeeTypeInUse(feeId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in isFeeTypeInUse", e);
            return false;
        }
    }

    // ==========================================
    //              BATCH PROCESSING
    // ==========================================

    /**
     * Batch job: Calculate monthly fees for all households (Mandatory fees only).
     * * @param month The target month.
     * @param year  The target year.
     * @return Number of invoices created, or -1 if failed/already calculated.
     */
    public int calculateMonthlyFees(int month, int year) {
        try {
            if (invoiceDAO.isMonthCalculated(month, year)) {
                logger.log(Level.WARNING, "[BillingController] Month {0}/{1} already calculated.", new Object[]{month, year});
                return -1;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error checking isMonthCalculated", e);
            return -1;
        }

        logger.log(Level.INFO, "[BillingController] Starting batch fee calculation for {0}/{1}", new Object[]{month, year});

        List<Household> households;
        List<Fee> fees;
        try {
            households = householdDAO.getAll();
            fees = feeDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error fetching data for batch", e);
            return -1;
        }

        int count = 0;
        Connection conn = null;

        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) return -1;
            conn.setAutoCommit(false);

            String sql = "INSERT INTO invoices (household_id, fee_type_id, month, year, amount_due, amount_paid, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Household hk : households) {
                    for (Fee fee : fees) {
                        if (fee.getType() == AppConstants.PHI_BAT_BUOC) { // 0: Mandatory
                            double amount = 0;
                            if ("m2".equalsIgnoreCase(fee.getUnit())) {
                                amount = fee.getUnitPrice() * hk.getArea();
                            } else {
                                amount = fee.getUnitPrice();
                            }
                            pstmt.setInt(1, hk.getId());
                            pstmt.setInt(2, fee.getId());
                            pstmt.setInt(3, month);
                            pstmt.setInt(4, year);
                            pstmt.setDouble(5, amount);
                            pstmt.setDouble(6, 0);
                            pstmt.setInt(7, 0);
                            pstmt.addBatch();
                            count++;
                        }
                    }
                }
                pstmt.executeBatch();
            }

            conn.commit();
            logger.log(Level.INFO, "[BillingController] Batch job completed. Created {0} invoices.", count);
            return count;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Batch job failed", e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return 0;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Calculates fee for a newly created Fee Type for all existing households.
     */
    public void calculateFeeForNewType(int month, int year, Fee fee) {
        if (fee.getType() != AppConstants.PHI_BAT_BUOC) return;

        logger.log(Level.INFO, "[BillingController] Auto-calculating for new fee: {0}", fee.getName());

        List<Household> households;
        try {
            households = householdDAO.getAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error fetching households", e);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) return;
            conn.setAutoCommit(false);

            String sql = "INSERT INTO invoices (household_id, fee_type_id, month, year, amount_due, amount_paid, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Household hk : households) {
                    double amount = 0;
                    if ("m2".equalsIgnoreCase(fee.getUnit())) {
                        amount = fee.getUnitPrice() * hk.getArea();
                    } else {
                        amount = fee.getUnitPrice();
                    }

                    pstmt.setInt(1, hk.getId());
                    pstmt.setInt(2, fee.getId());
                    pstmt.setInt(3, month);
                    pstmt.setInt(4, year);
                    pstmt.setDouble(5, amount);
                    pstmt.setDouble(6, 0);
                    pstmt.setInt(7, 0);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            logger.info("[BillingController] Invoices created for new fee type.");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in calculateFeeForNewType", e);
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Calculates vehicle fees (Cars & Motorbikes) for a specific month.
     * Looks for fees with specific units ("Car", "Motorbike").
     */
    public String calculateVehicleFees(int month, int year) {
        try {
            List<Fee> fees = feeDAO.getAll();
            int countCars = 0;
            int countMotorbikes = 0;

            for (Fee fee : fees) {
                // Ensure the database Fee Units are also updated to English ("Car", "Motorbike")
                String unit = fee.getUnit() != null ? fee.getUnit().trim() : "";

                // 1. Process Cars (VehicleType = 1)
                // Note: Make sure your DB data matches these English strings
                if ("Car".equalsIgnoreCase(unit) || "Oto".equalsIgnoreCase(unit)) { 
                    int rows = invoiceDAO.calculateVehicleFees(month, year, fee.getId(), fee.getUnitPrice(), 1);
                    countCars += rows;
                } 
                // 2. Process Motorbikes (VehicleType = 2)
                else if ("Motorbike".equalsIgnoreCase(unit) || "XeMay".equalsIgnoreCase(unit)) {
                    int rows = invoiceDAO.calculateVehicleFees(month, year, fee.getId(), fee.getUnitPrice(), 2);
                    countMotorbikes += rows;
                }
            }
            
            return String.format("Completed! Created invoices for %d Car households and %d Motorbike households.", countCars, countMotorbikes);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in calculateVehicleFees", e);
            return "System Error: " + e.getMessage();
        }
    }

    // ==========================================
    //              PAYMENT PROCESSING
    // ==========================================

    /**
     * Processes a payment for a specific invoice.
     * * @param invoiceId The ID of the invoice being paid.
     * @param amount    The amount paid.
     * @param payer     Name of the payer.
     * @param note      Optional note.
     * @return true if successful.
     */
    public boolean processPayment(int invoiceId, double amount, String payer, String note) {
        if (amount <= 0) return false;
        
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            Invoice invoice = invoiceDAO.getById(invoiceId);
            if (invoice == null) {
                conn.rollback();
                return false;
            }

            double totalPaid = invoice.getAmountPaid() + amount;
            boolean updateSuccess = invoiceDAO.updatePaymentStatus(invoiceId, totalPaid);
            if (!updateSuccess) {
                conn.rollback();
                return false;
            }

            Payment payment = new Payment(invoice.getHouseholdId(), invoice.getFeeTypeId(), amount, payer, note);
            if (paymentDAO.insert(payment)) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error in processPayment", e);
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
    
    public Household getHouseholdByRoomNumber(String roomNumber) {
        try {
            return householdDAO.getByRoomNumber(roomNumber);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[BillingController] Error getting household by room", e);
            return null;
        }
    }
}