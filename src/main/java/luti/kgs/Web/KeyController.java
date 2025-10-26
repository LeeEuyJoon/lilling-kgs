package luti.kgs.Web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import luti.kgs.Service.KeyService;

@RestController
@RequestMapping("/api/v1/key")
public class KeyController {

	private final KeyService keyService;

	public KeyController(KeyService keyService) {
		this.keyService = keyService;
	}

	@GetMapping("/next-block")
	public ResponseEntity<KeyBlock> allocateNextBlock() {
		KeyBlock keyBlock = keyService.allocateNextBlock();
		return ResponseEntity.ok(keyBlock);
	}
}
