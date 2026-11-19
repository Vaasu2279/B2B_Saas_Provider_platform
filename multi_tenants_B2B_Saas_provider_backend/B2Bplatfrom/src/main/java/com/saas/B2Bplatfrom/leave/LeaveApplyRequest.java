package com.saas.B2Bplatfrom.leave;


import java.time.LocalDate;

public record LeaveApplyRequest(LocalDate fromDate, LocalDate toDate, String reason) {}