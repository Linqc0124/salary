package com.salary.system.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 个人所得税计算工具类
 */
public class TaxCalculator {
    
    /**
     * 计算个人所得税
     *
     * @param salary 税前工资（应发工资）
     * @param socialInsurance 社保
     * @param housingFund 住房公积金
     * @param specialDeduction 专项附加扣除金额
     * @param month 当前月份（1-12）
     * @param previousTaxableIncome 前几个月累计的应纳税所得额
     * @param previousTaxPaid 前几个月累计已缴纳的税款
     * @return 应缴纳的个人所得税
     */
    public static BigDecimal calculateTax(BigDecimal salary, BigDecimal socialInsurance, BigDecimal housingFund,
                                         BigDecimal specialDeduction, int month, BigDecimal previousTaxableIncome, BigDecimal previousTaxPaid) {
        
        // 1. 计算当月应纳税所得额 = 工资收入 - 五险一金 - 起征点(5000) - 专项附加扣除
        BigDecimal baseDeduction = new BigDecimal("5000"); // 起征点5000元
        BigDecimal monthlyTaxableIncome = salary.subtract(socialInsurance).subtract(housingFund).subtract(baseDeduction).subtract(specialDeduction);
        
        if (monthlyTaxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            monthlyTaxableIncome = BigDecimal.ZERO;
        }
        
        // 2. 累计应纳税所得额 = 当月应纳税所得额 + 前几个月累计的应纳税所得额
        BigDecimal accumulatedTaxableIncome = monthlyTaxableIncome.add(previousTaxableIncome);
        
        // 3. 计算累计应纳税额
        BigDecimal accumulatedTax = calculateAccumulatedTax(accumulatedTaxableIncome);
        
        // 4. 当月应纳税额 = 累计应纳税额 - 已缴纳税额
        BigDecimal monthlyTaxAmount = accumulatedTax.subtract(previousTaxPaid);
        
        if (monthlyTaxAmount.compareTo(BigDecimal.ZERO) < 0) {
            monthlyTaxAmount = BigDecimal.ZERO;
        }
        
        return monthlyTaxAmount.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算累计应纳税额
     *
     * @param taxableIncome 累计应纳税所得额
     * @return 累计应纳税额
     */
    private static BigDecimal calculateAccumulatedTax(BigDecimal taxableIncome) {
        // 个人所得税税率表
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        } else if (taxableIncome.compareTo(new BigDecimal("36000")) <= 0) {
            // 不超过36000元的部分，税率3%
            return taxableIncome.multiply(new BigDecimal("0.03"));
        } else if (taxableIncome.compareTo(new BigDecimal("144000")) <= 0) {
            // 超过36000元至144000元的部分，税率10%
            return new BigDecimal("36000").multiply(new BigDecimal("0.03"))
                    .add(taxableIncome.subtract(new BigDecimal("36000")).multiply(new BigDecimal("0.1")));
        } else if (taxableIncome.compareTo(new BigDecimal("300000")) <= 0) {
            // 超过144000元至300000元的部分，税率20%
            return new BigDecimal("36000").multiply(new BigDecimal("0.03"))
                    .add(new BigDecimal("108000").multiply(new BigDecimal("0.1")))
                    .add(taxableIncome.subtract(new BigDecimal("144000")).multiply(new BigDecimal("0.2")));
        } else if (taxableIncome.compareTo(new BigDecimal("420000")) <= 0) {
            // 超过300000元至420000元的部分，税率25%
            return new BigDecimal("36000").multiply(new BigDecimal("0.03"))
                    .add(new BigDecimal("108000").multiply(new BigDecimal("0.1")))
                    .add(new BigDecimal("156000").multiply(new BigDecimal("0.2")))
                    .add(taxableIncome.subtract(new BigDecimal("300000")).multiply(new BigDecimal("0.25")));
        } else if (taxableIncome.compareTo(new BigDecimal("660000")) <= 0) {
            // 超过420000元至660000元的部分，税率30%
            return new BigDecimal("36000").multiply(new BigDecimal("0.03"))
                    .add(new BigDecimal("108000").multiply(new BigDecimal("0.1")))
                    .add(new BigDecimal("156000").multiply(new BigDecimal("0.2")))
                    .add(new BigDecimal("120000").multiply(new BigDecimal("0.25")))
                    .add(taxableIncome.subtract(new BigDecimal("420000")).multiply(new BigDecimal("0.3")));
        } else if (taxableIncome.compareTo(new BigDecimal("960000")) <= 0) {
            // 超过660000元至960000元的部分，税率35%
            return new BigDecimal("36000").multiply(new BigDecimal("0.03"))
                    .add(new BigDecimal("108000").multiply(new BigDecimal("0.1")))
                    .add(new BigDecimal("156000").multiply(new BigDecimal("0.2")))
                    .add(new BigDecimal("120000").multiply(new BigDecimal("0.25")))
                    .add(new BigDecimal("240000").multiply(new BigDecimal("0.3")))
                    .add(taxableIncome.subtract(new BigDecimal("660000")).multiply(new BigDecimal("0.35")));
        } else {
            // 超过960000元的部分，税率45%
            return new BigDecimal("36000").multiply(new BigDecimal("0.03"))
                    .add(new BigDecimal("108000").multiply(new BigDecimal("0.1")))
                    .add(new BigDecimal("156000").multiply(new BigDecimal("0.2")))
                    .add(new BigDecimal("120000").multiply(new BigDecimal("0.25")))
                    .add(new BigDecimal("240000").multiply(new BigDecimal("0.3")))
                    .add(new BigDecimal("300000").multiply(new BigDecimal("0.35")))
                    .add(taxableIncome.subtract(new BigDecimal("960000")).multiply(new BigDecimal("0.45")));
        }
    }
} 