
import com.printshop.service.MaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/warehouse/inventory")
@PreAuthorize("hasRole('WAREHOUSE')")
public class WarehouseInventoryController {
    private final MaterialService materialService;

    public WarehouseInventoryController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public String viewInventory(Model model) {
        model.addAttribute("materials", materialService.findAll());
        model.addAttribute("qualities", materialService.getAllQualities());
        model.addAttribute("weights", materialService.getAllWeights());
        return "warehouse/inventory";
    }
}
