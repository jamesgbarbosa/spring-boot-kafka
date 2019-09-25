package com.sample.service;

import com.sample.constants.ColorConstant;
import com.sample.consumer.Receiver;
import com.sample.model.Product;
import com.sample.producer.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    Sender sender;

    @Autowired
    private ProductRepository productRepository;

    public boolean saveToDatabase(Product record) throws SQLException {
        try {
//            Random random = new Random();
//            if (random.nextBoolean() ) {
                productRepository.save(record);
//                LOGGER.info(ColorConstant.ANSI_PURPLE+ "Saved!" + record.toString());
//            } else {
//                throw new SQLException();
            }
        } catch (Exception ex) {
            LOGGER.error(ColorConstant.ANSI_RED+"Saving to DB Failed");
            throw new SQLException(ex.getMessage());
        }
        return true;
    }
}
