package com.lamukhin.AntispamBot;

import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.Commands;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AntispamBotApplicationTests {

	@Value("${bot_owner_tg_id}")
	private long botOwnerId;

	@Autowired
	private AdminService adminService;

	@Test
	void contextLoads() {
		System.out.println(System.currentTimeMillis()+ 3 * 60 * 60 * 1000);
		//System.out.println(createListOfCommands(botOwnerId));
	}

	private List<BotCommand> createListOfCommands(Long userId) {
		List<BotCommand> listOfCommands = new ArrayList<>(
				Commands.getDefaultUserCommands()
		);
		if ((adminService.hasAdminStatusByUserId(userId)) || (userId.equals(botOwnerId))) {
			listOfCommands.addAll(Commands.getAdminCommands());        }
		if (userId.equals(botOwnerId)) {
			listOfCommands.addAll(Commands.getOwnerCommands());
		}
		return listOfCommands;
	}

}
