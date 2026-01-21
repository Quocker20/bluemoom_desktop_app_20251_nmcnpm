package com.bluemoon.app.controller.payment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bluemoon.app.dao.payment.PaymentDAO;
import com.bluemoon.app.model.Payment;

/**
 * Controller for managing payment transaction history.
 */
public class PaymentHistoryController {

    private static final Logger LOGGER = Logger.getLogger(PaymentHistoryController.class.getName());
    private final PaymentDAO paymentDAO;

    public PaymentHistoryController() {
        this.paymentDAO = new PaymentDAO();
    }

    /**
     * Retrieves payment history for a specific apartment/room.
     * * @param roomNumber The room number to filter by.
     * @return List of Payment objects.
     */
    public List<Payment> getPaymentsByRoomNumber(String roomNumber) {
        LOGGER.log(Level.INFO, "[PaymentHistoryController] Requesting payment history for room: {0}", roomNumber);
        List<Payment> result = new ArrayList<>();

        try {
            result = paymentDAO.getAllByRoomNumber(roomNumber);
            LOGGER.log(Level.INFO, "[PaymentHistoryController] Completed. Returning {0} records.", result.size());
            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PaymentHistoryController] Failed to fetch data for room {0}. Error: {1}",
                    new Object[] { roomNumber, e.getMessage() });
            return result;
        }
    }

    /**
     * Retrieves the entire payment history for all apartments.
     * * @return List of Payment objects.
     */
    public List<Payment> getAllPayments() {
        LOGGER.log(Level.INFO, "[PaymentHistoryController] Requesting ALL payment history.");
        List<Payment> result = new ArrayList<>();
        try {
            result = paymentDAO.getAll();
            LOGGER.log(Level.INFO, "[PaymentHistoryController] Completed. Total records: {0}", result.size());
            return result;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[PaymentHistoryController] Failed to fetch all data. Error: {0}",
                    e.getMessage());
            return result;
        }
    }
}