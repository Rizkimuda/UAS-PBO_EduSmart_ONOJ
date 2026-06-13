package com.edusmart.service;

import com.edusmart.model.Certificate;
import java.util.List;
import java.util.Optional;

public interface CertificateService {
    Certificate generateCertificate(Long userId, Long courseId);
    Optional<Certificate> getCertificate(Long userId, Long courseId);
    List<Certificate> getCertificatesByUser(Long userId);
}
