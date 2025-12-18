import { ReactNode } from 'react';
import { Logo } from './Logo';

interface PageContainerProps {
  children: ReactNode;
  showLogo?: boolean;
  showHelp?: boolean;
  onHelpClick?: () => void;
  steps?: string[];
  currentStep?: number;
}

export function PageContainer({
  children,
  showLogo = true,
  showHelp = true,
  onHelpClick,
  steps,
  currentStep,
}: PageContainerProps) {
  void steps;
  void currentStep;
  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-br from-[#ECE0DE] via-[#CDDCDC]/20 to-[#ECE0DE] p-6 md:p-8 lg:p-12">
      {/* Header */}
      {showLogo && (
        <header className="w-full flex justify-center mb-8 lg:mb-12">
          <Logo />
        </header>
      )}

      {/* Main Content */}
      <main className="flex-1 flex items-center justify-center w-full max-w-5xl mx-auto">
        {children}
      </main>

      {/* Footer */}
      {showHelp && (
        <footer className="w-full flex justify-center mt-8">
          <button
            onClick={onHelpClick}
            className="px-6 py-3 text-[#D3A67F] hover:text-[#C89769] transition-colors flex items-center gap-2"
          >
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <circle cx="12" cy="12" r="10" />
              <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
              <line x1="12" y1="17" x2="12.01" y2="17" />
            </svg>
            <span className="text-lg">Precisa de ajuda?</span>
          </button>
        </footer>
      )}
    </div>
  );
}
