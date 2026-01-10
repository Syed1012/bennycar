import { RegisterForm } from "@/components/auth/register-form";

export default function RegisterPage() {
  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-4rem)] py-12 px-4 bg-gradient-to-br from-[#f5ede4] via-[#fafaf8] to-[#e8d5c4]">
      <RegisterForm />
    </div>
  );
}
