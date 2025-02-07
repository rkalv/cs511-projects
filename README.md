# Wumpus World Agent - Simple Reflex Agent 

Rules Implemented

   1. Grab Gold Immediately - If the agent detects glitter, it picks up the gold using Action.GRAB.

   2. Shoot Wumpus - If a stench is detected, the agent shoots an arrow (Action.SHOOT) if available.

   3. Avoid Pits by Staying Still - If the agent senses a breeze, it means a pit is nearby. To prevent falling, the agent does NO_OP (nothing).

   4. Avoid Walls by Turning Left - If the agent bumps into a wall, it turns left (Action.TURN_LEFT) 80% of the time. To avoid getting stuck, it moves forward 20% of the time.

   5. Default Action: Move Forward to Explore - If no danger is detected, the agent moves forward (Action.GO_FORWARD).

The rules are arranged in order of increasing likelihood of occurrence. NO_OP was chosen for pits because taking any action could result in a -1000 penalty if the agent falls into a pit. For bumps, non-determinism was introduced since hitting a wall is not fatal. However, without variation, the agent could get stuck in a loop until timeout. By allowing a 20% chance to move forward, the agent has an opportunity to progress while minimizing the risk of getting trapped.
