package com.everton.FinTrack.controllers;

import com.everton.FinTrack.dtos.BoxSummaryDto;
import com.everton.FinTrack.dtos.TransactionRequestDto;
import com.everton.FinTrack.dtos.TransactionResponseDto;
import com.everton.FinTrack.enums.Category;
import com.everton.FinTrack.enums.MethodPayment;
import com.everton.FinTrack.enums.Type;
import com.everton.FinTrack.services.Impl.GoogleDriveService;
import com.everton.FinTrack.services.Impl.UserSevice;
import com.everton.FinTrack.services.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app")
@RequiredArgsConstructor
public class WebController {

    private final TransactionService transactionService;
    private final UserSevice userService;
    private final GoogleDriveService googleDriveService;

    // ======== LOGIN ========
    @GetMapping({"", "/", "/login"})
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        if (userService.authenticate(username, password)) {
            session.setAttribute("user", username);
            return "redirect:/app/home";
        } else {
            model.addAttribute("error", "Usuário ou senha inválidos!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/app/login";
    }

    // ======== HOME ========
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/app/login";

        LocalDate now = LocalDate.now();
        BoxSummaryDto box = getBoxSummary(now.getYear(), now.getMonthValue());

        model.addAttribute("username", user);
        model.addAttribute("boxTotalEntryFmt", formatBR(box.getTotalEntry()));
        model.addAttribute("boxTotalExpenseFmt", formatBR(box.getTotalExpense()));
        model.addAttribute("boxBalanceFmt", formatBR(box.getBalance()));
        model.addAttribute("box", box);
        model.addAttribute("transactions", transactionService.allTransaction());

        return "home";
    }

    // ======== LISTAR TRANSAÇÕES (SEM FILTRO) ========
    @GetMapping(value = "/transactions", params = {"!year", "!month", "!type"})
    public String showTransactions(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        List<TransactionResponseDto> transactions = transactionService.allTransaction();
        addTransactionsToModel(model, transactions, null, null);

        return "report"; // corrigido
    }

    // ======== LISTAR POR ANO E MÊS ========
    @GetMapping(value = "/transactions", params = {"year", "month"})
    public String listByMonth(@RequestParam int year,
                              @RequestParam int month,
                              Model model,
                              HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        List<TransactionResponseDto> transactions = transactionService.findTransactionByDate(year, month);
        addTransactionsToModel(model, transactions, year, month);

        return "report"; // corrigido
    }

    // ======== LISTAR POR TIPO ========
    @GetMapping(value = "/transactions", params = {"type"})
    public String listByType(@RequestParam Type type,
                             Model model,
                             HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        List<TransactionResponseDto> transactions = transactionService.findTransactionByType(type);
        addTransactionsToModel(model, transactions, null, null);

        return "report"; // corrigido
    }

    // ======== NOVA TRANSAÇÃO ========
    @GetMapping("/transactions/new")
    public String newTransactionForm(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";
        model.addAttribute("transactionForm", new TransactionForm());
        addDropdownEnums(model);
        return "add_transaction";
    }

    @PostMapping("/transactions")
    public String createTransaction(
            @ModelAttribute("transactionForm") TransactionForm form,
            @RequestParam(value = "file", required = false) MultipartFile file,
            RedirectAttributes redirect,
            HttpSession session
    ) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        try {
            String fileId = null;
            String fileUrl = null;

            if (file != null && !file.isEmpty()) {
                fileId = googleDriveService.uploadFile(file);
                fileUrl = googleDriveService.generateDriveFileLink(fileId);
            }

            TransactionRequestDto dto = toDto(form);
            dto.setReceiptFileId(fileId);
            dto.setReceiptFileUrl(fileUrl);

            transactionService.createTransaction(dto, fileId, fileUrl);

            redirect.addFlashAttribute("success", "Transação criada com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Erro ao salvar a transação: " + e.getMessage());
        }

        return "redirect:/app/transactions/new";
    }

    // ======== EDITAR TRANSAÇÃO ========
    @GetMapping("/transactions/edit/{id}")
    public String editTransaction(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        TransactionResponseDto dto = transactionService.findTransactionById(id);
        if (dto == null) {
            return "redirect:/app/transactions";
        }

        TransactionForm form = new TransactionForm();
        form.setValue(dto.getValue());
        form.setDate(dto.getDate());
        form.setType(dto.getType());
        form.setPayment(dto.getPayment());
        form.setCategory(dto.getCategory());
        form.setObservation(dto.getObservation());

        model.addAttribute("transactionForm", form);
        model.addAttribute("transactionId", id);
        addDropdownEnums(model);
        return "edit_transaction";
    }

    @PostMapping("/transactions/update/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @ModelAttribute("transactionForm") TransactionForm form,
                                    RedirectAttributes redirect,
                                    HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        try {
            TransactionRequestDto dto = toDto(form);
            transactionService.updateTransaction(id, dto);
            redirect.addFlashAttribute("success", "Transação atualizada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao atualizar: " + e.getMessage());
        }
        return "redirect:/app/transactions";
    }

    // ======== EXCLUIR TRANSAÇÃO ========
    @PostMapping("/transactions/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirect, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/app/login";

        try {
            transactionService.deletedTransaction(id);
            redirect.addFlashAttribute("success", "Transação excluída!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/app/transactions";
    }

    // ======== HELPERS ========
    private void addTransactionsToModel(Model model, List<TransactionResponseDto> transactions, Integer year, Integer month) {
        Map<Long, String> valueMap = transactions.stream()
                .collect(Collectors.toMap(TransactionResponseDto::getId, t -> formatBR(t.getValue())));

        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();
        int m = (month != null) ? month : now.getMonthValue();
        BoxSummaryDto box = getBoxSummary(y, m);

        model.addAttribute("transactions", transactions);
        model.addAttribute("valueMap", valueMap);
        model.addAttribute("types", Type.values());
        model.addAttribute("box", box);
        model.addAttribute("boxTotalEntryFmt", formatBR(box.getTotalEntry()));
        model.addAttribute("boxTotalExpenseFmt", formatBR(box.getTotalExpense()));
        model.addAttribute("boxBalanceFmt", formatBR(box.getBalance()));
        addDropdownEnums(model);
    }

    private String formatBR(BigDecimal value) {
        if (value == null) return "0,00";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(value);
    }

    private BoxSummaryDto getBoxSummary(int year, int month) {
        BoxSummaryDto box = transactionService.calculateTotalBalance(year, month);
        return (box != null) ? box : new BoxSummaryDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private TransactionRequestDto toDto(TransactionForm form) {
        TransactionRequestDto dto = new TransactionRequestDto();
        dto.setValue(form.getValue());
        dto.setDate(form.getDate());
        dto.setType(form.getType());
        dto.setPayment(form.getPayment());
        dto.setCategory(form.getCategory());
        dto.setObservation(form.getObservation());
        return dto;
    }

    private void addDropdownEnums(Model model) {
        model.addAttribute("types", Type.values());
        model.addAttribute("payments", MethodPayment.values());
        model.addAttribute("categories", Category.values());
    }

    @GetMapping("/app/report")
    public String reportPage(Model model) {
        List<TransactionResponseDto> transactions = transactionService.allTransaction();
        model.addAttribute("transactions", transactions);
        return "report";
    }
}