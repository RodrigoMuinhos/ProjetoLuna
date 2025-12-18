import { PageContainer } from '../PageContainer';
import { FlowType, getFlowSteps } from '@/lib/flowSteps';

interface AsaasPixPaymentProps {
  onComplete: () => void;
  onBack?: () => void;
  flow?: FlowType;
}

export function AsaasPixPayment({ onComplete, onBack, flow = 'payment' }: AsaasPixPaymentProps) {
  const steps = getFlowSteps(flow);
  const currentStep = flow === 'checkin' ? 3 : 2;

  return (
    <PageContainer steps={steps} currentStep={currentStep}>
      <div className="w-full flex flex-col items-center gap-8 text-center">
        <div className="space-y-3">
          <h2 className="text-3xl md:text-4xl text-[#D3A67F]">
            Pagamento externo
          </h2>
          <p className="text-xl text-[#4A4A4A]/70">Processado via parceiro LunaPay</p>
        </div>

        <div className="bg-white rounded-2xl shadow-lg p-8 max-w-md space-y-6 text-left">
          <p className="text-lg text-[#4A4A4A]">
            Os pagamentos agora são processados pela LunaPay. Enviamos o link de
            pagamento pelos canais combinados (SMS/WhatsApp/e-mail). Conclua o
            pagamento por lá e confirme para prosseguir.
          </p>
          <ul className="list-disc list-inside space-y-2 text-sm text-[#4A4A4A]/80">
            <li>Não armazenamos dados de pagamento neste totem.</li>
            <li>Se o link não chegou, peça ao atendente reenviar.</li>
            <li>Após pagar, clique em &ldquo;Continuar&rdquo; para avançar.</li>
          </ul>
        </div>

        <div className="flex flex-col gap-3 w-full max-w-xs">
          <button
            onClick={onComplete}
            className="px-8 py-3 bg-[#D3A67F] text-white rounded-lg hover:bg-[#C89769] transition"
          >
            Continuar
          </button>
          {onBack && (
            <button
              onClick={onBack}
              className="px-8 py-3 border border-[#D3A67F] text-[#D3A67F] rounded-lg hover:bg-[#F6EFE9] transition"
            >
              Voltar
            </button>
          )}
        </div>
      </div>
    </PageContainer>
  );
}
