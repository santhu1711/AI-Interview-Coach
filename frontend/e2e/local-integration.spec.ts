import { expect, test, type Page } from "@playwright/test";

const API = "http://localhost:8080";
const password = "Strong1!Password";
const unique = (prefix: string) => `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}@example.com`;

async function register(page: Page, fullName: string, email: string) {
  await page.goto("/register");
  await page.getByLabel("Full name").fill(fullName);
  await page.getByLabel("Email").fill(email);
  await page.getByLabel("Password", { exact: true }).fill(password);
  await page.getByLabel("Confirm password").fill(password);
  await page.getByRole("button", { name: "Create account" }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
}

async function login(page: Page, email: string) {
  await page.goto("/login");
  await page.getByLabel("Email").fill(email);
  await page.getByLabel("Password", { exact: true }).fill(password);
  await page.getByRole("button", { name: "Log in" }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
}

async function token(page: Page) {
  return page.evaluate(() => localStorage.getItem("ai_interview_coach_access_token"));
}

async function configureInterview(page: Page, category: "IT" | "NON_IT") {
  await page.goto("/interview/setup");
  await expect(page.getByText("Choose your field")).toBeVisible();
  if (category === "IT") {
    await page.getByRole("button", { name: /^IT Field/ }).click();
    await page.getByLabel("Technical Domain").selectOption("JAVA");
    await page.getByLabel("Interview topic").fill("Spring API integration");
    await page.getByLabel("Interview mode").selectOption("TECHNICAL");
    await page.getByLabel("Target role").fill("Java Developer");
  } else {
    await page.getByRole("button", { name: /^Non-IT Field/ }).click();
    await page.getByLabel("Professional Domain").selectOption("CUSTOMER_SUPPORT");
    await page.getByLabel("Interview topic").fill("Customer escalation handling");
    await page.getByLabel("Interview mode").selectOption("SITUATIONAL");
    await page.getByLabel("Target role").fill("Customer Support Specialist");
  }
  await page.getByLabel("Experience level").selectOption("BEGINNER");
  await page.getByLabel("Difficulty").selectOption("MEDIUM");
  await page.getByLabel("Number of questions").selectOption("5");
  await page.getByRole("button", { name: "Start interview" }).click();
  await expect(page).toHaveURL(/\/interview\/[0-9a-f-]{36}$/);
  return page.url().split("/").pop()!;
}

test.describe.serial("real local integration", () => {
  test("IT browser flow, refresh, follow-up, automatic completion, report, analytics, profile, ownership, and auth boundaries", async ({ page, request }) => {
    await page.goto("/dashboard");
    await expect(page).toHaveURL(/\/login\?next=%2Fdashboard$/);

    const email = unique("phase11-it");
    await register(page, "Phase Eleven IT", email);
    await page.getByRole("button", { name: "Logout" }).click();
    await expect(page).toHaveURL("http://localhost:3000/");
    await login(page, email);

    const sessionId = await configureInterview(page, "IT");
    await expect(page.getByText("What core concept from the selected technical domain would you apply here, and why?")).toBeVisible();
    await page.reload();
    await expect(page.getByText("What core concept from the selected technical domain would you apply here, and why?")).toBeVisible();

    const answer = page.getByLabel("Your answer");
    await answer.fill("This is a partial answer that needs more detail.");
    await page.getByRole("button", { name: /Submit answer/ }).click();
    await expect(page.getByText("Could you expand on the most important missing part?")).toBeVisible();
    for (let index = 0; index < 6; index++) {
      if (await page.getByRole("link", { name: "View results" }).isVisible().catch(() => false)) break;
      await answer.fill(`Strong integrated answer ${index} with a clear technical trade-off.`);
      await page.getByRole("button", { name: /Submit answer/ }).click();
      await expect.poll(async () => await page.getByRole("link", { name: "View results" }).isVisible().catch(() => false) || await answer.inputValue().then(value => value === "").catch(() => true)).toBe(true);
    }
    await page.getByRole("link", { name: "View results" }).click();
    await page.getByRole("button", { name: "Generate report" }).click();
    await expect(page.getByText("You passed")).toBeVisible();
    await expect(page.getByText("Technical accuracy")).toBeVisible();

    await page.getByRole("link", { name: "History" }).click();
    await expect(page.getByText("Spring API integration")).toBeVisible();
    await page.getByLabel("Topic or target role").fill("Spring API");
    await page.getByLabel("Sort").selectOption("HIGHEST_SCORE");
    await page.getByRole("button", { name: "Apply filters" }).click();
    await expect(page.getByText("Spring API integration")).toBeVisible();

    await page.getByRole("link", { name: "Dashboard" }).click();
    await expect(page.getByText("Total interviews")).toBeVisible();
    await expect(page.getByRole("heading", { name: "Performance", exact: true })).toBeVisible();
    await page.getByRole("link", { name: "Profile" }).click();
    await expect(page.getByLabel("Email")).toHaveValue(email);
    await expect(page.getByLabel("Email")).toBeDisabled();
    await page.getByLabel("Full name").fill("Updated Integration User");
    await page.getByRole("button", { name: "Save changes" }).click();
    await expect(page.getByText("Profile updated successfully.")).toBeVisible();

    const firstToken = await token(page);
    expect(firstToken).toBeTruthy();
    const secondEmail = unique("phase11-owner");
    const secondRegistration = await request.post(`${API}/api/auth/register`, { data: { fullName: "Other Owner", email: secondEmail, password, confirmPassword: password } });
    expect(secondRegistration.status()).toBe(201);
    const secondToken = (await secondRegistration.json()).accessToken as string;
    const wrongOwnerRead = await request.get(`${API}/api/interviews/${sessionId}`, { headers: { Authorization: `Bearer ${secondToken}` } });
    expect(wrongOwnerRead.status()).toBe(404);
    const wrongOwnerDelete = await request.delete(`${API}/api/interviews/${sessionId}`, { headers: { Authorization: `Bearer ${secondToken}` } });
    expect(wrongOwnerDelete.status()).toBe(404);

    await page.getByRole("button", { name: "Logout" }).click();
    await page.goto("/dashboard");
    await expect(page).toHaveURL(/\/login\?next=%2Fdashboard$/);
    await page.evaluate(() => localStorage.setItem("ai_interview_coach_access_token", "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjQxMDI0NDQ4MDB9.invalid"));
    await page.goto("/dashboard");
    await expect(page).toHaveURL(/\/login\?next=%2Fdashboard$/);
    await expect.poll(() => page.evaluate(() => localStorage.getItem("ai_interview_coach_access_token"))).toBeNull();
  });

  test("Non-IT creation, answer, manual completion, report, abandonment, pagination, and persisted MySQL data", async ({ page, request }) => {
    const email = unique("phase11-nonit");
    await register(page, "Phase Eleven Non IT", email);
    const sessionId = await configureInterview(page, "NON_IT");
    await expect(page.getByText("What challenging workplace situation have you handled, and what did you do?")).toBeVisible();
    await page.getByLabel("Your answer").fill("I listened, clarified the issue, proposed a resolution, and confirmed satisfaction.");
    await page.getByRole("button", { name: /Submit answer/ }).click();
    await expect(page.getByLabel("Your answer")).toHaveValue("");
    await page.getByRole("button", { name: "End interview" }).click();
    await page.getByRole("button", { name: "Complete interview" }).click();
    await page.getByRole("link", { name: "View results" }).click();
    await page.getByRole("button", { name: "Generate report" }).click();
    await expect(page.getByText("Situational judgement")).toBeVisible();
    await expect(page.getByText("Role understanding")).toBeVisible();

    const authToken = await token(page);
    expect(authToken).toBeTruthy();
    for (let index = 0; index < 11; index++) {
      const created = await request.post(`${API}/api/interviews`, { headers: { Authorization: `Bearer ${authToken}` }, data: { fieldCategory: "NON_IT", interviewDomain: "CUSTOMER_SUPPORT", customDomain: null, topic: `Pagination session ${index}`, difficulty: "EASY", interviewMode: "SITUATIONAL", targetRole: "Support Specialist", experienceLevel: "BEGINNER", totalQuestions: 5 } });
      expect(created.status()).toBe(201);
    }
    await page.goto("/history");
    await expect(page.getByText(/12 interviews/)).toBeVisible();
    await expect(page.getByRole("button", { name: "Next", exact: true })).toBeEnabled();
    await page.getByRole("button", { name: "Next", exact: true }).click();
    await expect(page.getByText("Page 2 of 2")).toBeVisible();

    const abandonId = await configureInterview(page, "NON_IT");
    await page.getByRole("button", { name: "Abandon" }).click();
    await page.getByRole("button", { name: "Abandon interview" }).click();
    await expect(page.getByText("Interview abandoned")).toBeVisible();
    const persisted = await request.get(`${API}/api/interviews/${abandonId}`, { headers: { Authorization: `Bearer ${authToken}` } });
    expect(persisted.status()).toBe(200);
    expect((await persisted.json()).status).toBe("ABANDONED");
    expect(sessionId).toMatch(/^[0-9a-f-]{36}$/);
  });

  test("real API validation, CORS, invalid session, loading, network failure, expired token, and mobile viewport", async ({ page, request }) => {
    const originAllowed = await request.fetch(`${API}/api/interview-options`, { method: "OPTIONS", headers: { Origin: "http://localhost:3000", "Access-Control-Request-Method": "GET", "Access-Control-Request-Headers": "authorization" } });
    expect(originAllowed.status()).toBe(200);
    expect(originAllowed.headers()["access-control-allow-origin"]).toBe("http://localhost:3000");
    const originRejected = await request.fetch(`${API}/api/interview-options`, { method: "OPTIONS", headers: { Origin: "http://evil.example", "Access-Control-Request-Method": "GET" } });
    expect(originRejected.status()).toBe(403);
    const invalidLogin = await request.post(`${API}/api/auth/login`, { data: { email: "missing@example.com", password: "wrong" } });
    expect(invalidLogin.status()).toBe(401);
    const anonymous = await request.get(`${API}/api/dashboard/summary`);
    expect(anonymous.status()).toBe(401);

    const email = unique("phase11-errors");
    await register(page, "Error State User", email);
    await page.goto("/interview/00000000-0000-0000-0000-000000000000");
    await expect(page.getByText("Interview not found.")).toBeVisible();

    await page.route(`${API}/api/interview-options`, async route => { await new Promise(resolve => setTimeout(resolve, 600)); await route.continue(); });
    await page.goto("/interview/setup");
    await expect(page.getByText("Loading interview options…")).toBeVisible();
    await expect(page.getByText("Choose your field")).toBeVisible();
    await page.unroute(`${API}/api/interview-options`);
    await page.route(`${API}/api/interview-options`, route => route.abort("connectionfailed"));
    await page.reload();
    await expect(page.getByText("Unable to load interview options. Check your connection and try again.")).toBeVisible();
    await expect(page.getByRole("button", { name: "Try again" })).toBeVisible();
    await page.unroute(`${API}/api/interview-options`);

    const expiredPayload = Buffer.from(JSON.stringify({ exp: 1 })).toString("base64url");
    await page.evaluate(value => localStorage.setItem("ai_interview_coach_access_token", value), `header.${expiredPayload}.signature`);
    await page.goto("/profile");
    await expect(page).toHaveURL(/\/login\?next=%2Fprofile$/);
    await page.setViewportSize({ width: 375, height: 812 });
    await page.goto("/");
    await expect(page.getByRole("heading", { name: "Practise smarter. Interview with confidence." })).toBeVisible();
    const overflow = await page.evaluate(() => document.documentElement.scrollWidth > document.documentElement.clientWidth);
    expect(overflow).toBe(false);
  });
});
