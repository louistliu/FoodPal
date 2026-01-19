| Key          | Value                           |
|--------------|---------------------------------|
| Date:        | 2025-01-16                      |
| Time:        | 13:45-14:30                     |
| Location:    | DW PC Hall 2                    |
| Chair        | Vilius                          |
| Minute Taker | Floyd                           |
| Attendees:   | Vilius, Omer, Floyd, Ege, Louis |
---
## Agenda Items:
*   Opening by chair (3 min)
    *   Current status of base requirements & optional requirements
	    * Feedback: Couldn't edit ingredients (being merged in)
	    * Once ege merges his blocking changes in Floyd can do Config (last base requirement)
*   Check-in: How is everyone doing + reflect on work done (7 min)
    *   What is each person doing
	    * Omer: Language switching => The language switch works perfectly, only saving left
	    * Louis: Something about enums
	    * Ege: Done with favorites, will change according to MR feedback.
	    * Floyd: Done with Recipe server 
    *   When merge out
	    * Team: today (friday)
    *   What bugs have encountered
	    * None
*   Announcements by the team (1 min)
	* No announcements by the team
*   Approval of the agenda, does anyone have any additions? (2 min)
	* No additions
*   Approval of last minutes - Did everyone read the last minutes? Do you approve of these? (1 min)
	* Everyone approved the last minutes.
*   Announcements by the TA & questions (4 min)
	* Keep in mind the formative feedback isn't *foolproof*, the final assessment might be more thorough. If something breaks in the final assessment you still might get partial points.
	* Next week (w9) one last closing meeting, with a different location
    *  Q: Should test db (wiped every time) be enabled or the persistent one?
	    * Whatever we prefer.
	* Q: Should we check duplicate named ingredients on server or client side?
		* Doesn't really matter much, it would be good if an error screen pops up then.
	* Warnings about personal process requirement should be coming out the evening of 16-1
*   Presentation of the current app to TA (1.5 min)
	* Presented app.
	* QTA: 
	* Q: Add Info button (so  the user knows there's a right clicking options)?
		* It's intuitive enough, but add it if you want.
	* QTA: Will you take out the save button?
		* Yes.
	* QTA: Can you edit (information in) the screens when an error popup shows up?
		* No.
	* TA: Add config options & info (default name/location) to README.md
*   Talking Points: (inform / brainstorm / decision-making / Discuss)
    *   Discuss - Who focuses on quality assurance + base requirements + refactor and who focuses on new feature implementation (4 min)
	    * Tuesday at 9:30 meeting, discuss how far we are.
	    * Before the meeting: finish current features & finish testing & fix those bugs
    *   Decision - How should we deal with MRs in the final week (2 min)
	    * Thursday by midday last MR's so we have time to review, fix and merge.
        *  Require approving and contributing persons to extensively test program before approving, revert back to earlier version on main if we find a major bug
	* Inform - Any other bugs found or urgent changes needed?
		* Omer: duplicating one (duplicate) recipe a lot (on main)
		* Floyd & Vilius: [#61](https://gitlab.ewi.tudelft.nl/cse1105/2025-2026/teams/csep-team-49/-/issues/61) could not or very limited reproduce issue of ingredients sometimes not showing up in the dropdown
		* Louis: Dummy recipes not showing up on boot (works after restart) \[possibly because of outdated branch]
    *   Decision - when should final changes be uploaded (1 min)
        *   Should expect gitlab to be down during the final few days.
		*   New code may break main, introduce hidden bugs.
    *   Discuss - Potential tasks in the upcoming sprint (5 min)
	    * We do not really want to do JavaFX testing unless we have to do a lot of more time. (need to be headless). Discuss Tuesday if we have time/ a desire for this. 
    *   Decision - Allocate existing work to team members (4 min)
        *   Add time spent and estimate to old git issues
	        * Every assignee do it for their own issues.
	        * Louis: check if everyone did this right.
        *   Refactor for dependency injection (checking if right)
	        * Vilius (most knowledge)
        *   Remove template code
	        * Floyd (done, ready to merge)
        *   Recipe integration testing
	        * Floyd (done, ready to push)
        *   Ingredient integration testing
	        * Omer
        *   Create dummy data for db
	        * Ege
        *   Frontend method tests
	        * Prioritize finished new features, then testing. No assignee.
    *   Decision - next chair + minute taker (1 min)
	    * No minute taker and chair, 1 talking-point per person 
*   Summarize action points: Who, what, when? (2 min)
	-   Ege: Merge blocking changes. (This is a prerequisite for Floyd's next task)
	-   Floyd: Do Config (last base requirement). (This can be done once Ege merges his blocking changes)
	-   Team: Finish current features, finish testing, and fix bugs. (To be completed before the Tuesday 9:30 meeting)
	-   Team: Ensure all last Merge Requests (MRs) are submitted by Thursday midday. (To allow time for review, fixes, and merging)
	-   Approving and contributing persons: Extensively test the program before approving MRs. Revert to an earlier version on main if a major bug is found.
	-   Floyd: Add config options & info (default name/location) to `README.md`.
	-   Every assignee: Add time spent and estimate to their old git issues.
	-   Louis: Check if everyone correctly added time spent and estimates to their issues.
	-   Vilius: Refactor for dependency injection (checking if right).
	-   Omer: Ingredient integration testing.
	-   Ege: Create dummy data for the database.
	-   Team (no specific assignee, prioritize): Perform Frontend method tests. (Prioritize finished new features, then testing)
*   Feedback : What went well and what can be improved next time? (2 min)
	* Everything went well, 
*   Planned meeting duration != actual duration? Where / why did you misestimate? (2 min)
	* Perfectly on time.
*   Question round: Does anyone have anything to add before the meeting closes? (2 min)
	* No other questions
*   Closure (1 min)